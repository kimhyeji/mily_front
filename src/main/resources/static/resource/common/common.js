toastr.options = {
    closeButton: true,
    debug: false,
    newestOnTop: true,
    progressBar: true,
    positionClass: "toast-top-center",
    preventDuplicates: false,
    onclick: null,
    showDuration: "300",
    hideDuration: "1000",
    timeOut: "5000",
    extendedTimeOut: "1000",
    showEasing: "swing",
    hideEasing: "linear",
    showMethod: "fadeIn",
    hideMethod: "fadeOut"
};

function parseMsg(msg) {
    const [pureMsg, ttl] = msg.split(";ttl=");

    const currentJsUnixTimestamp = new Date().getTime();

    if (ttl && parseInt(ttl) + 5000 < currentJsUnixTimestamp) {
        return [pureMsg, false];
    }

    return [pureMsg, true];
}

function parseMsg(msg) {
    const [pureMsg, ttl] = msg.split(";ttl=");

    const currentJsUnixTimestamp = new Date().getTime();

    if (ttl && parseInt(ttl) + 5000 < currentJsUnixTimestamp) {
        return [pureMsg, false];
    }

    return [pureMsg, true];
}

function toastMsg(isNotice, msg) {
    if ( isNotice ) toastNotice(msg);
    else toastWarning(msg);
}

function toastNotice(msg) {
    const [pureMsg, needToShow] = parseMsg(msg);

    if (needToShow) {
        toastr["success"](pureMsg, "알림");
    }
}

function toastWarning(msg) {
    const [pureMsg, needToShow] = parseMsg(msg);

    if (needToShow) {
        toastr["warning"](pureMsg, "경고");
    }
}

// URL에서 msg 파라미터의 값을 가져오는 함수
function getMsgFromURL() {
    const url = new URL(window.location.href);
    return url.searchParams.get('msg');
}
function getFailMsgFromURL() {
    const url = new URL(window.location.href);
    return url.searchParams.get('failMsg');
}

const message = getMsgFromURL();

// msg 파라미터의 값이 있으면 toastr로 알림을 표시
if (message) {
    toastNotice(decodeURIComponent(message));
}

const msg = getMsgFromURL();

// msg 파라미터의 값이 있으면 toastr로 알림을 표시
if (msg) {
    toastNotice(decodeURIComponent(msg));
}

// history.back 에 의해서 돌아온 경우에 실행됨
$(window).bind("pageshow", function (event) {
    let localStorageKeyAboutHistoryBackFailMsg = "historyBackFailMsg___" + location.href;

    if (!localStorage.getItem(localStorageKeyAboutHistoryBackFailMsg)) {
        localStorageKeyAboutHistoryBackFailMsg = "historyBackFailMsg___null";
    }

    const historyBackFailMsg = localStorage.getItem(localStorageKeyAboutHistoryBackFailMsg);
    if (historyBackFailMsg) {
        toastWarning(historyBackFailMsg);
        localStorage.removeItem(localStorageKeyAboutHistoryBackFailMsg);
    }
});