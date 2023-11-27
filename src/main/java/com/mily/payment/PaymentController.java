package com.mily.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MilyUserService milyUserService;
    private final PaymentService paymentService;
    private String lastOrderName;

    @PostConstruct
    private void init () {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
    }

    private final String SECRET_KEY = "test_sk_nRQoOaPz8LLWOwWPNP1P8y47BMw6";

    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public String doPayment(Model model) {
        try {
            MilyUser isLoginedUser = milyUserService.getCurrentUser();
            if (isLoginedUser != null) {
                model.addAttribute("user", isLoginedUser);
            } else {
                return "redirect:/user/login";
            }
            return "mily/payment/payment";
        } catch (NullPointerException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/getordername")
    public ResponseEntity<String> getOrderName (@RequestParam(value = "orderName") String orderName) {
        lastOrderName = orderName;
        return ResponseEntity.ok().body(orderName);
    }

    @RequestMapping("/success")
    public String confirmPayment(
            @RequestParam String paymentKey, @RequestParam String orderId,
            @RequestParam Long amount, Model model) throws Exception {

        MilyUser isLoginedUser = milyUserService.getCurrentUser();
        model.addAttribute("user", isLoginedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode successNode = responseEntity.getBody();
            String formattedAmount = paymentService.letFormattedAmount(amount);

            model.addAttribute("orderId", successNode.get("orderId").asText());
            model.addAttribute("orderName", lastOrderName);
            model.addAttribute("amount", formattedAmount);

            String secret = successNode.get("secret").asText();

            paymentService.doPayment(orderId, isLoginedUser, lastOrderName, amount);
            milyUserService.getPoint(isLoginedUser, lastOrderName);
            return "mily/payment/success";
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "mily/payment/fail";
        }
    }

    @RequestMapping("/fail")
    public String failPayment (
            @RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "mily/payment/fail";
    }

    @RequestMapping("/virtual-account/callback")
    @ResponseStatus(HttpStatus.OK)
    public void handleVirtualAccountCallback(@RequestBody CallbackPayload payload) {
        if (payload.getStatus().equals("DONE")) {
            // handle deposit result
        }
    }

    public static class CallbackPayload {
        public CallbackPayload() {}

        private String secret;
        private String status;
        private String orderId;

        public String getSecret() {
            return secret;
        }

        public void setSecret (String secret) {
            this.secret = secret;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
}