var modal = document.getElementById("modalImages");
var modalImg = document.getElementById("modalImg");
var captionText = document.getElementById("caption");
function abrirModal(n) {
    var img = document.getElementsByClassName("modalImage")[n - 1];
    modal.style.display = "block";
    modalImg.src = img.src;
    captionText.innerHTML = img.alt;
}
;

var span = document.getElementsByClassName("close")[0];

span.onclick = function () {
    modal.style.display = "none";
};