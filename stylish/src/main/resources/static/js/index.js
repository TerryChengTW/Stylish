document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category') || 'all';
    fetchCampaignData();
    fetchProductData(category);
});

function fetchCampaignData() {
    console.log('Fetching campaign data...');
    const apiUrl = 'http://18.182.90.188/api/1.0/marketing/campaigns';

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Received data:', data);
            if (data.data && data.data.length > 0) {
                updateCampaign(data.data[0]);
            }
        })
        .catch(error => console.error('Error fetching campaign data:', error));
}

function updateCampaign(campaign) {
    const campaignElement = document.getElementById('campaign');
    const storyElement = document.getElementById('campaign-story');

    campaignElement.style.backgroundImage = `url('${campaign.picture}')`;

    let storyHTML = campaign.story.replace(/\r?\n/g, '<br>');

    const lines = storyHTML.split('<br>');
    if (lines.length > 0) {
        const lastLine = lines.pop();
        storyHTML = lines.join('<br>') + `<br><span class="last-line">${lastLine}</span>`;
    }

    storyElement.innerHTML = `<h2>${storyHTML}</h2>`;

    campaignElement.onclick = function() {
        window.location.href = `/product.html?id=${campaign.product_id}`;
    };
}

function fetchProductData(category = 'all') {
    console.log('Fetching product data...');
    const apiUrl = `http://18.182.90.188/api/1.0/products/${category}`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Received product data:', data);
            if (data.data && data.data.length > 0) {
                displayProducts(data.data);
            }
        })
        .catch(error => console.error('Error fetching product data:', error));
}

function displayProducts(products) {
    const productGrid = document.querySelector('.product-grid');
    productGrid.innerHTML = '';

    products.forEach(product => {
        const productElement = document.createElement('div');
        productElement.className = 'product-item';
        productElement.innerHTML = `
            <img src="${product.main_image}" alt="${product.title}">
            <div class="color-options">
                ${product.colors.map(color => `<span class="color-option" style="background-color: ${color.code};"></span>`).join('')}
            </div>
            <h3>${product.title}</h3>
            <p>TWD. ${product.price}</p>
        `;
        productElement.addEventListener('click', () => {
            window.location.href = `/product.html?id=${product.id}`;
        });
        productGrid.appendChild(productElement);
    });
}