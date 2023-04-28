const BASE_UTL = 'http://localhost:8081/search/';

var search;
var article;
var articleId;

const insertDom = document.getElementById('insert');
const deleteDom = document.getElementById('delNode');
const showDom = document.getElementById('show');
const searchDom = document.getElementById('search');
searchDom.addEventListener('click', (e) => {
  fetch(BASE_UTL + 'search?search=' + searchDom.value, {
    method: 'GET'
  }).then((result) => {
    console.log(result);
    show(result.data.data);
  });
});

deleteDom.addEventListener('click', (e) => {
  fetch(BASE_UTL + 'delete', {
    method: 'GET',
    body: (articleId = this.articleId),
  }).then((response) => {
    delNode(this.articleId);
    if (response.data.code == 4000) {
      this.$message.success(response);
    } else {
      this.$message.error(error);
    }
  });
});

insertDom.addEventListener('click', (e) => {
  fetch(BASE_UTL + 'add', {
    method: 'POST',
    body: JSON.stringify((article = this.article)),
    headers: new Headers({
      'Content-Type': 'application/json',
    }),
  }).then((response) => {
    if (response.data.code == 4000) {
      this.$message.success(response);
    } else {
      this.$message.error(error);
    }
  });
});

function delNode(id) {
  const target = document.getElementById(id);
  showDom.removeChild(target);
}

function clear() {
  showDom.innerHTML = '';
}

function show(data) {
  if (!(data instanceof Array)) {
    return;
  }
  for (const d of data) {
    const divDom = document.createElement('div');
    const btnDom = document.createElement('button');
    btnDom.classList.add('del');
    divDom.id = d.id;
    divDom.innerText = d.content;
    divDom.appendChild(btnDom);
    showDom.appendChild(divDom);
  }
}
