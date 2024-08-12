document.addEventListener('DOMContentLoaded', () => {
    let headerFooterLoaded = setInterval(() => {
        if (document.getElementById('header-placeholder').children.length > 0 &&
            document.getElementById('footer-placeholder').children.length > 0) {
            clearInterval(headerFooterLoaded);
            initializeProfilePage();
        }
    }, 100);
});

window.fbAsyncInit = function() {
    FB.init({
        appId      : '929525328895382',
        cookie     : true,
        xfbml      : true,
        version    : 'v20.0'
    });
};

function initializeProfilePage() {
    checkLoginStatus();
}

function checkLoginStatus() {
    const token = localStorage.getItem('accessToken');
    const userData = JSON.parse(localStorage.getItem('userData'));
    if (token && userData) {
        displayUserProfile(userData);
    } else {
        showLoginForm();
    }
}

function fetchUserProfile(token) {
    fetch('/api/1.0/user/profile', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            showLoginForm();
        } else {
            displayUserProfile(data);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showLoginForm();
    });
}

function displayUserProfile(userData) {
    const profileContent = document.getElementById('profile-content');
    profileContent.innerHTML = `
        <h2>歡迎，${userData.name}</h2>
        <p>電子郵件：${userData.email}</p>
        <img src="${userData.picture}" alt="用戶頭像" class="profile-picture">
        <button id="logout" class="btn-logout">登出</button>
    `;
    document.getElementById('logout').addEventListener('click', handleLogout);
}

function showLoginForm() {
    const profileContent = document.getElementById('profile-content');
    profileContent.innerHTML = `
        <h2>請登入</h2>
        <form id="loginForm" class="login-form">
            <input type="email" id="email" required placeholder="電子郵件">
            <input type="password" id="password" required placeholder="密碼">
            <button type="submit" class="btn-login">登入</button>
        </form>
        <button id="fbLoginBtn" class="btn-facebook">使用 Facebook 登入</button>
        <p>還沒有帳號？<a href="#" id="showSignup">註冊</a></p>
    `;
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('showSignup').addEventListener('click', showSignupForm);
    document.getElementById('fbLoginBtn').addEventListener('click', handleFacebookLogin);
}

function showSignupForm() {
    const profileContent = document.getElementById('profile-content');
    profileContent.innerHTML = `
        <h2>註冊新帳號</h2>
        <form id="signupForm" class="signup-form">
            <input type="text" id="name" required placeholder="姓名">
            <input type="email" id="email" required placeholder="電子郵件">
            <input type="password" id="password" required placeholder="密碼">
            <button type="submit" class="btn-signup">註冊</button>
        </form>
        <p>已有帳號？<a href="#" id="showLogin">登入</a></p>
    `;
    document.getElementById('signupForm').addEventListener('submit', handleSignup);
    document.getElementById('showLogin').addEventListener('click', showLoginForm);
}

function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    const loginData = {
        provider: "native",
        email: email,
        password: password
    };
    
    fetch('/api/1.0/user/signin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 403) {
                throw new Error('帳號或密碼錯誤');
            }
            throw new Error('登入失敗');
        }
        return response.json();
    })
    .then(result => {
        if (!result.data || !result.data.access_token) {
            throw new Error('登入失敗：無效的回應格式');
        }
        localStorage.setItem('accessToken', result.data.access_token);
        localStorage.setItem('userData', JSON.stringify(result.data.user));
        displayUserProfile(result.data.user);
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message);
        document.getElementById('password').value = '';
    });
}

function handleSignup(event) {
    event.preventDefault();
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    const signupData = {
        name: name,
        email: email,
        password: password
    };
    
    fetch('/api/1.0/user/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(signupData),
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            throw new Error(data.error);
        }
        console.log('註冊成功,正在自動登入...');
        const loginEvent = new Event('submit');
        document.getElementById('email').value = email;
        document.getElementById('password').value = password;
        handleLogin(loginEvent);
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || '註冊失敗，請稍後再試。');
    });
}

function handleLogout() {
    fetch('/api/1.0/user/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Logout failed');
            }
            localStorage.removeItem('accessToken');
            localStorage.removeItem('userData');
            localStorage.removeItem('fbAccessToken');
            showLoginForm();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('登出失敗，請稍後再試。');
        });
}

function handleFacebookLogin() {
    FB.login(function(response) {
        if (response.authResponse) {
            console.log('Facebook login successful');
            var accessToken = response.authResponse.accessToken;
            loginWithFacebook(accessToken);
        } else {
            console.log('Facebook login failed');
        }
    }, {scope: 'public_profile,email'});
}

function loginWithFacebook(accessToken) {
    fetch('/api/1.0/user/signin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            provider: 'facebook',
            access_token: accessToken
        })
    })
    .then(response => response.json())
    .then(result => {
        if (result.error) {
            throw new Error(result.error);
        }
        if (!result.data || !result.data.access_token) {
            throw new Error('登入失敗：無效的回應格式');
        }
        localStorage.setItem('accessToken', result.data.access_token);
        localStorage.setItem('userData', JSON.stringify(result.data.user));
        displayUserProfile(result.data.user);
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message || 'Facebook 登入失敗，請稍後再試。');
    });
}