let currentProduct;

document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    initializeTapPay();

    if (productId) {
        fetchProductDetails(productId);
    } else {
        console.error('No product ID provided');
    }
});

async function fetchProductDetails(productId) {
    try {
        const apiUrl = `http://18.182.90.188/api/1.0/products/details?id=${productId}`;
    
        const response = await fetch(apiUrl);
        const data = await response.json();
        currentProduct = data.data;
        displayProductDetails(currentProduct);
    } catch (error) {
        console.error('Error fetching product details:', error);
    }
}

function displayProductDetails(product) {
    document.getElementById('main-image').src = product.main_image;
    document.getElementById('product-title').innerText = product.title;
    document.getElementById('product-id').innerText = `商品編號：${product.id}`;
    document.getElementById('product-price').innerText = `TWD.${product.price}`;
    document.getElementById('product-texture').innerText = `材質：${product.texture}`;
    document.getElementById('product-wash').innerText = `清洗：${product.wash}`;
    document.getElementById('product-place').innerText = `產地：${product.place}`;
    document.getElementById('product-description').innerText = product.description;

    const colorOptions = document.getElementById('color-options');
    colorOptions.innerHTML = '';
    product.colors.forEach(color => {
        const colorDiv = document.createElement('div');
        colorDiv.className = 'color-option';
        colorDiv.title = color.name;
        const innerDiv = document.createElement('div');
        innerDiv.style.backgroundColor = color.code;
        innerDiv.style.width = '100%';
        innerDiv.style.height = '100%';
        colorDiv.appendChild(innerDiv);
        colorOptions.appendChild(colorDiv);
    });

    const sizeOptions = document.getElementById('size-options');
    sizeOptions.innerHTML = '';
    product.sizes.forEach(size => {
        const sizeDiv = document.createElement('div');
        sizeDiv.className = 'size-option';
        sizeDiv.innerText = size;
        sizeOptions.appendChild(sizeDiv);
    });

    const additionalImages = document.getElementById('additional-images');
    additionalImages.innerHTML = '';
    product.images.forEach(imageUrl => {
        const img = document.createElement('img');
        img.src = imageUrl;
        additionalImages.appendChild(img);
    });

    document.querySelectorAll('.color-option').forEach(option => {
        option.addEventListener('click', function() {
            document.querySelectorAll('.color-option').forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');
            updateStock();
        });
    });

    document.querySelectorAll('.size-option').forEach(option => {
        option.addEventListener('click', function() {
            document.querySelectorAll('.size-option').forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');
            updateStock();
        });
    });

    function updateStock() {
        const selectedColor = document.querySelector('.color-option.selected');
        const selectedSize = document.querySelector('.size-option.selected');
        const addToCartButton = document.getElementById('add-to-cart');
        const quantityInput = document.getElementById('quantity');
    
        if (selectedColor) {
            const selectedColorCode = rgbToHex(selectedColor.querySelector('div').style.backgroundColor);
            document.querySelectorAll('.size-option').forEach(option => {
                const variant = product.variants.find(variant =>
                    variant.color_code.toUpperCase() === selectedColorCode.toUpperCase() &&
                    variant.size === option.innerText
                );
                if (variant) {
                    option.disabled = false;
                    option.style.opacity = 1;
                } else {
                    option.disabled = true;
                    option.style.opacity = 0.5;
                }
            });
        }
    
        if (selectedSize) {
            const selectedSizeText = selectedSize.innerText;
            document.querySelectorAll('.color-option').forEach(option => {
                const variant = product.variants.find(variant =>
                    variant.size === selectedSizeText &&
                    variant.color_code.toUpperCase() === rgbToHex(option.querySelector('div').style.backgroundColor).toUpperCase()
                );
                if (variant) {
                    option.disabled = false;
                    option.style.opacity = 1;
                } else {
                    option.disabled = true;
                    option.style.opacity = 0.5;
                }
            });
        }
    
        if (selectedColor && selectedSize) {
            const selectedColorCode = rgbToHex(selectedColor.querySelector('div').style.backgroundColor);
            const selectedSizeText = selectedSize.innerText;
            const variant = product.variants.find(variant =>
                variant.color_code.toUpperCase() === selectedColorCode.toUpperCase() &&
                variant.size === selectedSizeText
            );
            if (variant) {
                quantityInput.max = variant.stock;
                quantityInput.value = Math.min(quantityInput.value, variant.stock);
                addToCartButton.innerText = "立即購買";
                addToCartButton.disabled = false;
            } else {
                quantityInput.max = 0;
                quantityInput.value = 0;
                addToCartButton.innerText = "缺貨";
                addToCartButton.disabled = true;
            }
        } else {
            addToCartButton.innerText = "請選擇尺寸";
            addToCartButton.disabled = true;
        }
    }

    document.getElementById('decrease').addEventListener('click', function() {
        const quantityInput = document.getElementById('quantity');
        const currentValue = parseInt(quantityInput.value);
        if (currentValue > 1) {
            quantityInput.value = currentValue - 1;
        }
    });

    document.getElementById('increase').addEventListener('click', function() {
        const quantityInput = document.getElementById('quantity');
        const currentValue = parseInt(quantityInput.value);
        const maxValue = parseInt(quantityInput.max);
        if (currentValue < maxValue) {
            quantityInput.value = currentValue + 1;
        }
    });

    updateStock();
}

function rgbToHex(rgb) {
    if (rgb.startsWith('#')) {
        return rgb;
    }
    const rgbValues = rgb.match(/\d+/g);
    if (!rgbValues || rgbValues.length < 3) return '';
    return '#' + rgbValues.map(x => {
        const hex = parseInt(x).toString(16);
        return hex.length === 1 ? '0' + hex : hex;
    }).join('');
}

function isUserLoggedIn() {
    return localStorage.getItem('accessToken') !== null;
}

function initializeTapPay() {
    if (typeof TPDirect !== 'undefined') {
        TPDirect.setupSDK(12348, 'app_pa1pQcKoY22IlnSXq5m5WP5jFKzoRG58VEXpT7wU62ud7mMbDOGzCYIlzzLF', 'sandbox');
    } else {
        console.error('TPDirect is not loaded. Please check the script inclusion.');
    }
}


document.getElementById('add-to-cart').addEventListener('click', function() {
    if (!isUserLoggedIn()) {
        window.location.href = '/profile.html';
        return;
    }
    
    const modal = document.getElementById('checkout-modal');
    modal.style.display = "block";

    TPDirect.card.setup({
        fields: {
            number: {
                element: '#card-number',
                placeholder: '**** **** **** ****'
            },
            expirationDate: {
                element: '#card-expiration-date',
                placeholder: 'MM / YY'
            },
            ccv: {
                element: '#card-ccv',
                placeholder: 'ccv'
            }
        },
        styles: {
            'input': {
                'color': 'gray'
            },
            ':focus': {
                'color': 'black'
            },
            '.valid': {
                'color': 'green'
            },
            '.invalid': {
                'color': 'red'
            }
        }
    });
});

document.getElementById('submit-order').addEventListener('click', function() {
    const tappayStatus = TPDirect.card.getTappayFieldsStatus();

    if (tappayStatus.canGetPrime === false) {
        alert('信用卡資訊填寫不完整');
        return;
    }

    TPDirect.card.getPrime((result) => {
        if (result.status !== 0) {
            alert('get prime error ' + result.msg);
            return;
        }
        
        const prime = result.card.prime;
        
        const selectedColor = document.querySelector('.color-option.selected');
        const selectedSize = document.querySelector('.size-option.selected');
        const quantity = document.getElementById('quantity').value;

        if (!selectedColor || !selectedSize || !quantity || !currentProduct) {
            console.error('Missing required order data');
            alert('訂單資料不完整，請重新選擇商品');
            return;
        }

        const order = {
            "prime": prime,
            "order": {
                "shipping": "delivery",
                "payment": "credit_card",
                "subtotal": currentProduct.price * quantity,
                "freight": 14,
                "total": currentProduct.price * quantity + 14,
                "recipient": {
                    "name": "Luke",
                    "phone": "0987654321",
                    "email": "luke@gmail.com",
                    "address": "市政府站",
                    "time": "morning"
                },
                "list": [
                    {
                        "id": currentProduct.id,
                        "name": currentProduct.title,
                        "price": currentProduct.price,
                        "color": {
                            "code": rgbToHex(selectedColor.querySelector('div').style.backgroundColor),
                            "name": selectedColor.title
                        },
                        "size": selectedSize.innerText,
                        "qty": parseInt(quantity)
                    }
                ]
            }
        };

        sendOrder(order);
    });
});

function sendOrder(order) {
    console.log('Sending order data:', JSON.stringify(order, null, 2));

    fetch('http://18.182.90.188/api/1.0/order/checkout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(order)
    })
    .then(response => {
        console.log('Response status:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('Response data:', data);
        if (data.data && data.data.number) {
            alert(`訂單成功！訂單編號：${data.data.number}`);
            window.location.href = '/thankyou.html';
        } else {
            alert('訂單失敗：' + (data.error || '未知錯誤'));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('訂單發送失敗，請稍後再試');
    });
}

document.querySelector('.close').addEventListener('click', function() {
    document.getElementById('checkout-modal').style.display = "none";
});
