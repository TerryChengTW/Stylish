document.addEventListener('DOMContentLoaded', function() {
    setupHeaderNavigation();
});

function setupHeaderNavigation() {
    document.querySelectorAll('.navigate-bar a').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const category = this.getAttribute('href').split('=')[1];
            window.location.href = `/index.html?category=${category}`;
        });
    });
}
