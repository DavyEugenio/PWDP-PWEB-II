var content = document.getElementById("content");
var complementar = document.getElementById("complementar");
var divcomplementar = document.getElementsByClassName("divcomplementar")[0];
function tamanho() {
    if (content.offsetHeight < (window.innerHeight - 122)) {
        complementar.style.height = (window.innerHeight - 122 - content.offsetHeight) + "px";
        divcomplementar.style.display = "block";
    } else {
        divcomplementar.style.display = "none";
    }
}
onresize = tamanho;
onload = tamanho;