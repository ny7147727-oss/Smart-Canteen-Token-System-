/**
 * SmartServe Web Engine — Modern Student Food Ordering & Token Management System
 */

// Comprehensive Canteen Food Menu with Categories, Descriptions & Images
const CANTEEN_FOOD_ITEMS = [
  { id: 1, name: "Chole Bhature", price: 80.00, category: "North Indian", description: "Fluffy fried bhature served with rich spicy chickpea curry & pickle", image: "images/food/chole_bhature.jpg" },
  { id: 2, name: "Rajma Chawal", price: 70.00, category: "North Indian", description: "Classic Punjabi comfort meal of red kidney beans & steamed basmati rice", image: "images/food/rajma_chawal.jpg" },
  { id: 3, name: "Dal Makhani", price: 90.00, category: "North Indian", description: "Creamy slow-cooked black lentils with fresh butter and aromatic spices", image: "images/food/dal_makhani.jpg" },
  { id: 4, name: "Kadhai Paneer", price: 100.00, category: "North Indian", description: "Cottage cheese cubes tossed with bell peppers and freshly ground kadhai masala", image: "images/food/kadhai_paneer.jpg" },
  { id: 5, name: "Shahi Paneer", price: 110.00, category: "North Indian", description: "Royal paneer dish in a rich, creamy, mildly sweet tomato-cashew gravy", image: "images/food/shahi_paneer.jpg" },
  { id: 6, name: "Aloo Paratha", price: 50.00, category: "North Indian", description: "Whole wheat flatbread stuffed with spiced mashed potatoes, served with butter", image: "images/food/aloo_paratha.jpg" },
  { id: 7, name: "Paneer Paratha", price: 70.00, category: "North Indian", description: "Crispy golden paratha stuffed with seasoned crumbled cottage cheese", image: "images/food/paneer_paratha.jpg" },
  { id: 8, name: "Poori Sabzi", price: 60.00, category: "North Indian", description: "Golden puffed puris served with tangy spicy potato gravy", image: "images/food/poori_sabzi.jpg" },
  { id: 9, name: "Chole Kulche", price: 65.00, category: "North Indian", description: "Soft leavened bread paired with zesty Delhi-style dry chickpea curry", image: "images/food/chole_kulche.jpg" },
  { id: 10, name: "Kadhi Chawal", price: 70.00, category: "North Indian", description: "Crispy gram flour fritters in spiced yogurt curry with steamed rice", image: "images/food/kadhi_chawal.jpg" },
  { id: 11, name: "Gol Gappe", price: 40.00, category: "Chaat", description: "Crispy hollow puris filled with tangy spicy mint water & potatoes", image: "images/food/gol_gappe.jpg" },
  { id: 12, name: "Aloo Tikki", price: 45.00, category: "Chaat", description: "Crisp pan-fried spiced potato patties topped with sweet and mint chutneys", image: "images/food/aloo_tikki.jpg" },
  { id: 13, name: "Dahi Bhalla", price: 55.00, category: "Chaat", description: "Soft lentil dumplings soaked in creamy sweetened yogurt and tamarind glaze", image: "images/food/dahi_bhalla.jpg" },
  { id: 14, name: "Papdi Chaat", price: 50.00, category: "Chaat", description: "Crisp flour wafers layered with boiled potatoes, chickpeas, and yogurts", image: "images/food/papdi_chaat.jpg" },
  { id: 15, name: "Samosa Chaat", price: 50.00, category: "Chaat", description: "Crushed crispy samosas smothered in savory chole and tangy chutneys", image: "images/food/samosa_chaat.jpg" },
  { id: 16, name: "Bhel Puri", price: 40.00, category: "Chaat", description: "Puffed rice tossed with crunchy sev, onions, tomatoes, and tangy tamarind sauce", image: "images/food/bhel_puri.jpg" },
  { id: 17, name: "Sev Puri", price: 45.00, category: "Chaat", description: "Crispy flat papdis loaded with spiced potatoes, onions, and fine crunchy sev", image: "images/food/sev_puri.jpg" },
  { id: 18, name: "Dahi Puri", price: 55.00, category: "Chaat", description: "Mini crisp shells filled with potatoes, sweetened curd, and spicy mint drizzle", image: "images/food/dahi_puri.jpg" },
  { id: 19, name: "Raj Kachori", price: 70.00, category: "Chaat", description: "Grand crispy king kachori stuffed with sprouts, yogurt, and festive chutneys", image: "images/food/raj_kachori.jpg" },
  { id: 20, name: "Chole Chaat", price: 45.00, category: "Chaat", description: "Warm tangy chickpeas tossed with green chilies, tomatoes, and chaat masala", image: "images/food/chole_chaat.jpg" },
  { id: 21, name: "Sprouts Salad", price: 60.00, category: "Healthy", description: "Nutrient-packed sprouted moong tossed with lemon, cucumber, and herbs", image: "images/food/sprouts_salad.jpg" },
  { id: 22, name: "Fruit Bowl", price: 60.00, category: "Healthy", description: "Seasonal assortment of fresh diced fruits with a dash of chaat masala", image: "images/food/fruit_bowl.jpg" },
  { id: 23, name: "Vegetable Salad", price: 45.00, category: "Healthy", description: "Crunchy garden-fresh cucumber, carrot, tomato, and beet salad", image: "images/food/vegetable_salad.jpg" },
  { id: 24, name: "Paneer Salad", price: 75.00, category: "Healthy", description: "High-protein fresh paneer cubes tossed with bell peppers and herbs", image: "images/food/paneer_salad.jpg" },
  { id: 25, name: "Oats Bowl", price: 50.00, category: "Healthy", description: "Wholesome warm oats cooked to perfection with honey and roasted seeds", image: "images/food/oats_bowl.jpg" },
  { id: 26, name: "Vegetable Sandwich", price: 45.00, category: "Healthy", description: "Multi-grain slices layered with crisp vegetables and green chutney", image: "images/food/vegetable_sandwich.jpg" },
  { id: 27, name: "Idli", price: 35.00, category: "South Indian", description: "Steamed fluffy fermented rice cakes served with authentic coconut chutney", image: "images/food/idli.jpg" },
  { id: 28, name: "Poha", price: 40.00, category: "Breakfast", description: "Light tempered flattened rice with mustard seeds, peanuts, and fresh coriander", image: "images/food/poha.jpg" },
  { id: 29, name: "Upma", price: 35.00, category: "Breakfast", description: "Savory South-style semolina porridge tempered with curry leaves and mustard", image: "images/food/upma.jpg" },
  { id: 30, name: "Masala Dosa", price: 60.00, category: "South Indian", description: "Crispy golden crepe stuffed with spiced potato masala, served with sambar", image: "images/food/masala_dosa.jpg" },
  { id: 31, name: "Idli Sambar", price: 40.00, category: "South Indian", description: "Steamed soft idlis immersed in piping hot tangy lentil vegetable stew", image: "images/food/idli_sambar.jpg" },
  { id: 32, name: "Samosa", price: 20.00, category: "Snacks", description: "Crispy golden pastry crust filled with spiced potatoes and green peas", image: "images/food/samosa.jpg" },
  { id: 33, name: "Veg Sandwich", price: 45.00, category: "Snacks", description: "Toasted sandwich loaded with fresh veggies and melted cheese spread", image: "images/food/veg_sandwich.jpg" },
  { id: 34, name: "Veg Momos", price: 50.00, category: "Snacks", description: "Delicate steamed dumplings stuffed with finely minced seasoned vegetables", image: "images/food/veg_momos.jpg" },
  { id: 35, name: "Paneer Roll", price: 70.00, category: "Snacks", description: "Warm whole wheat wrap filled with juicy spiced paneer and crunchy onions", image: "images/food/paneer_roll.jpg" },
  { id: 36, name: "Spring Roll", price: 55.00, category: "Snacks", description: "Crispy golden fried rolls packed with shredded stir-fried oriental vegetables", image: "images/food/spring_roll.jpg" },
  { id: 37, name: "Bread Pakora", price: 25.00, category: "Snacks", description: "Spiced potato-stuffed bread slice dipped in gram flour batter and deep fried", image: "images/food/bread_pakora.jpg" },
  { id: 38, name: "Kachori", price: 25.00, category: "Snacks", description: "Flaky deep-fried pastry filled with savory spiced moong dal mixture", image: "images/food/kachori.jpg" },
  { id: 39, name: "Tea", price: 15.00, category: "Beverages", description: "Authentic Indian masala chai brewed with aromatic cardamom and fresh ginger", image: "images/food/tea.jpg" },
  { id: 40, name: "Coffee", price: 20.00, category: "Beverages", description: "Freshly brewed rich aromatic hot coffee with frothy milk", image: "images/food/coffee.jpg" },
  { id: 41, name: "Cold Coffee", price: 45.00, category: "Beverages", description: "Chilled thick blended coffee with creamy milk and rich chocolate drizzle", image: "images/food/cold_coffee.jpg" },
  { id: 42, name: "Fresh Lime", price: 20.00, category: "Beverages", description: "Refreshing sweet & salted lime soda with a pinch of black salt", image: "images/food/fresh_lime.jpg" },
  { id: 43, name: "Masala Chaas", price: 25.00, category: "Beverages", description: "Traditional spiced cooling buttermilk seasoned with roasted cumin and mint", image: "images/food/masala_chaas.jpg" },
  { id: 44, name: "Lassi", price: 35.00, category: "Beverages", description: "Creamy Punjabi sweet yogurt drink topped with a layer of fresh malai", image: "images/food/lassi.jpg" },
  { id: 45, name: "Mango Shake", price: 50.00, category: "Beverages", description: "Thick luscious mango milkshake prepared from ripe sweet mango pulp", image: "images/food/mango_shake.jpg" },
  { id: 46, name: "Banana Shake", price: 45.00, category: "Beverages", description: "Creamy energizing banana milkshake blended with fresh milk and honey", image: "images/food/banana_shake.jpg" }
];

const DEFAULT_USERS = [
  { id: 1, username: 'admin', password: 'admin123', role: 'ADMIN', fullName: 'Canteen Admin' },
  { id: 2, username: 'rahul', password: 'rahul123', role: 'STUDENT', fullName: 'Rahul Kumar', rollNumber: 'CS2023001' }
];

const DEFAULT_ORDERS = [
  {
    id: 101,
    tokenNumber: 101,
    studentName: 'Rahul Kumar',
    studentRoll: 'CS2023001',
    items: [{ id: 1, name: 'Chole Bhature', price: 80.00, quantity: 1, subtotal: 80.00 }, { id: 43, name: 'Masala Chaas', price: 25.00, quantity: 1, subtotal: 25.00 }],
    amount: 105.00,
    paymentMethod: 'UPI',
    paymentStatus: 'SUCCESS',
    status: 'READY',
    orderTime: '12:30 PM'
  },
  {
    id: 102,
    tokenNumber: 102,
    studentName: 'Priya Sharma',
    studentRoll: 'EC2023045',
    items: [{ id: 2, name: 'Rajma Chawal', price: 70.00, quantity: 1, subtotal: 70.00 }],
    amount: 70.00,
    paymentMethod: 'Card',
    paymentStatus: 'SUCCESS',
    status: 'PREPARING',
    orderTime: '12:34 PM'
  },
  {
    id: 103,
    tokenNumber: 103,
    studentName: 'Amit Verma',
    studentRoll: 'ME2023012',
    items: [{ id: 32, name: 'Samosa', price: 20.00, quantity: 2, subtotal: 40.00 }, { id: 39, name: 'Tea', price: 15.00, quantity: 2, subtotal: 30.00 }],
    amount: 70.00,
    paymentMethod: 'Demo Wallet',
    paymentStatus: 'SUCCESS',
    status: 'QUEUED',
    orderTime: '12:38 PM'
  }
];

// App State
let state = {
  currentUser: null,
  foodItems: CANTEEN_FOOD_ITEMS,
  users: JSON.parse(localStorage.getItem('sc_web_users') || 'null') || DEFAULT_USERS,
  orders: JSON.parse(localStorage.getItem('sc_web_orders') || 'null') || DEFAULT_ORDERS,
  cart: {}, // { itemId: quantity }
  tokenCounter: parseInt(localStorage.getItem('sc_web_token_counter') || '104', 10),
  activeOrder: null,
  searchQuery: '',
  selectedCategory: 'All',
  selectedPaymentMethod: 'UPI',
  simulatePaymentSuccess: true
};

function saveState() {
  localStorage.setItem('sc_web_users', JSON.stringify(state.users));
  localStorage.setItem('sc_web_orders', JSON.stringify(state.orders));
  localStorage.setItem('sc_web_token_counter', state.tokenCounter.toString());
}

// Toast Notifications
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `<span>${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</span> <span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 250);
  }, 3000);
}

// View Router
function showView(viewId) {
  document.querySelectorAll('.view-section').forEach(el => el.classList.remove('active'));
  const target = document.getElementById(`view-${viewId}`);
  if (target) {
    target.classList.add('active');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
  updateHeader();

  if (viewId === 'menu') renderMenu();
  if (viewId === 'cart') renderCart();
  if (viewId === 'payment') renderPaymentView();
  if (viewId === 'token-status') renderTokenStatus();
  if (viewId === 'admin-dashboard') renderAdminDashboard();
  if (viewId === 'live-orders') renderLiveOrders();
}

function updateHeader() {
  const headerRight = document.getElementById('header-right');
  if (!headerRight) return;

  if (!state.currentUser) {
    headerRight.innerHTML = ``;
  } else {
    headerRight.innerHTML = `
      <span class="user-tag">👤 <strong>${state.currentUser.fullName}</strong> (${state.currentUser.role})</span>
      <button class="btn btn-sm btn-secondary" onclick="logout()">Logout</button>
    `;
  }
}

// Authentication
function setDemoLogin(role) {
  if (role === 'student') {
    document.getElementById('login-username').value = 'rahul';
    document.getElementById('login-password').value = 'rahul123';
    document.getElementById('pill-student').classList.add('active');
    document.getElementById('pill-admin').classList.remove('active');
  } else {
    document.getElementById('login-username').value = 'admin';
    document.getElementById('login-password').value = 'admin123';
    document.getElementById('pill-admin').classList.add('active');
    document.getElementById('pill-student').classList.remove('active');
  }
}

function handleLogin(e) {
  e.preventDefault();
  const u = document.getElementById('login-username').value.trim();
  const p = document.getElementById('login-password').value;

  const user = state.users.find(x => x.username.toLowerCase() === u.toLowerCase() && x.password === p);
  if (!user) {
    showToast('Invalid username or password.', 'error');
    return;
  }

  state.currentUser = user;
  showToast(`Welcome, ${user.fullName}!`, 'success');
  if (user.role === 'ADMIN') {
    showView('admin-dashboard');
  } else {
    showView('menu');
  }
}

function handleRegister(e) {
  e.preventDefault();
  const fullName = document.getElementById('reg-fullname').value.trim();
  const rollNo = document.getElementById('reg-rollno').value.trim();
  const username = document.getElementById('reg-username').value.trim();
  const password = document.getElementById('reg-password').value;
  const confirmPassword = document.getElementById('reg-confirmpassword').value;

  if (password.length < 6) {
    showToast('Password must be at least 6 characters.', 'error');
    return;
  }
  if (password !== confirmPassword) {
    showToast('Passwords do not match.', 'error');
    return;
  }
  if (state.users.some(x => x.username.toLowerCase() === username.toLowerCase())) {
    showToast('Username already exists. Please choose another username.', 'error');
    return;
  }

  const newUser = {
    id: state.users.length + 1,
    username,
    password,
    role: 'STUDENT',
    fullName,
    rollNumber: rollNo
  };

  state.users.push(newUser);
  saveState();
  showToast('Registration successful! Please sign in.', 'success');
  showView('login');
}

function logout() {
  state.currentUser = null;
  state.cart = {};
  state.selectedCategory = 'All';
  state.searchQuery = '';
  showToast('Logged out successfully.', 'info');
  showView('login');
}

// Menu & Cart Functions
function getCartTotalCount() {
  return Object.values(state.cart).reduce((sum, qty) => sum + qty, 0);
}

function getCartTotalPrice() {
  return Object.entries(state.cart).reduce((total, [itemId, qty]) => {
    const item = state.foodItems.find(x => x.id === parseInt(itemId, 10));
    return total + (item ? item.price * qty : 0);
  }, 0);
}

function handleSearchInput(e) {
  state.searchQuery = e.target.value.trim().toLowerCase();
  const clearBtn = document.getElementById('search-clear-btn');
  if (clearBtn) {
    clearBtn.style.display = state.searchQuery ? 'block' : 'none';
  }
  renderMenu();
}

function clearSearch() {
  state.searchQuery = '';
  const searchInput = document.getElementById('menu-search-input');
  if (searchInput) searchInput.value = '';
  const clearBtn = document.getElementById('search-clear-btn');
  if (clearBtn) clearBtn.style.display = 'none';
  renderMenu();
}

function setCategoryFilter(category, btnElement) {
  state.selectedCategory = category;
  document.querySelectorAll('.category-chip').forEach(chip => {
    chip.classList.toggle('active', chip.getAttribute('data-category') === category);
  });
  renderMenu();
}

function clearSearchAndCategory() {
  state.searchQuery = '';
  state.selectedCategory = 'All';
  const searchInput = document.getElementById('menu-search-input');
  if (searchInput) searchInput.value = '';
  const clearBtn = document.getElementById('search-clear-btn');
  if (clearBtn) clearBtn.style.display = 'none';
  document.querySelectorAll('.category-chip').forEach(chip => {
    chip.classList.toggle('active', chip.getAttribute('data-category') === 'All');
  });
  renderMenu();
}

function renderMenu() {
  // Update Hero Dynamic Greeting
  const heroGreetingEl = document.getElementById('hero-greeting');
  if (heroGreetingEl) {
    const hour = new Date().getHours();
    let timeGreeting = "Good Afternoon";
    if (hour < 12) timeGreeting = "Good Morning";
    else if (hour >= 17) timeGreeting = "Good Evening";

    const studentName = state.currentUser ? (state.currentUser.fullName || state.currentUser.username) : "Student";
    heroGreetingEl.textContent = `${timeGreeting}, ${studentName}! 👋`;
  }

  // Update Cart Header Button
  const cartBtn = document.getElementById('cart-header-btn');
  const totalCount = getCartTotalCount();
  const totalPrice = getCartTotalPrice();

  if (cartBtn) {
    cartBtn.innerHTML = `🛒 View Cart (${totalCount}) — ₹${totalPrice.toFixed(2)}`;
    if (totalCount > 0) {
      cartBtn.classList.add('has-items');
    } else {
      cartBtn.classList.remove('has-items');
    }
  }

  // Filter Food Items by Category and Search Query
  let filtered = state.foodItems;
  if (state.selectedCategory && state.selectedCategory !== 'All') {
    filtered = filtered.filter(item => item.category === state.selectedCategory);
  }
  if (state.searchQuery) {
    const q = state.searchQuery;
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(q) || 
      (item.category && item.category.toLowerCase().includes(q)) ||
      (item.description && item.description.toLowerCase().includes(q))
    );
  }

  // Update Item Count Label
  const countLabel = document.getElementById('food-count-label');
  if (countLabel) {
    const categorySuffix = state.selectedCategory !== 'All' ? ` in ${state.selectedCategory}` : '';
    const searchSuffix = state.searchQuery ? ` matching "${state.searchQuery}"` : '';
    countLabel.textContent = `Showing ${filtered.length} food items${categorySuffix}${searchSuffix}`;
  }

  const grid = document.getElementById('food-cards-grid');
  if (!grid) return;

  if (filtered.length === 0) {
    grid.innerHTML = `
      <div class="empty-search-box">
        <div class="empty-icon">🍽️</div>
        <h3 class="empty-title">No food items found</h3>
        <p class="empty-subtitle">We couldn't find any dishes matching your search query. Try searching for something else or explore all categories.</p>
        <button class="btn btn-primary" onclick="clearSearchAndCategory()" style="margin-top: 14px;">
          View All Food Items
        </button>
      </div>
    `;
    return;
  }

  grid.innerHTML = filtered.map(item => {
    const qty = state.cart[item.id] || 0;
    const catClass = (item.category || 'general').toLowerCase().replace(/\s+/g, '-');
    const categoryBadge = item.category ? `<span class="card-category-badge badge-${catClass}">${item.category}</span>` : '';
    const itemDesc = item.description || 'Freshly prepared delicious campus canteen meal.';
    const imgSrc = item.image || 'images/food/default_food.jpg';

    let actionHtml = '';
    if (qty > 0) {
      actionHtml = `
        <div class="card-qty-control">
          <button class="card-qty-btn btn-minus" onclick="updateItemQuantity(${item.id}, -1)" title="Decrease quantity">−</button>
          <span class="card-qty-value">${qty}</span>
          <button class="card-qty-btn btn-plus" onclick="updateItemQuantity(${item.id}, 1)" title="Increase quantity">+</button>
        </div>
      `;
    } else {
      actionHtml = `
        <button class="btn-add-food" onclick="updateItemQuantity(${item.id}, 1)">
          + Add
        </button>
      `;
    }

    return `
      <div class="food-card ${qty > 0 ? 'in-cart' : ''}" data-id="${item.id}">
        <div class="food-card-img-wrapper">
          <img src="${imgSrc}" alt="${item.name}" class="food-card-img" loading="lazy" onerror="this.onerror=null; this.src='images/food/default_food.jpg';">
          <div class="food-card-veg-tag" title="100% Pure Vegetarian">
            <span class="veg-dot"></span>
          </div>
          ${categoryBadge}
        </div>
        <div class="food-card-body">
          <h3 class="food-card-title">${item.name}</h3>
          <p class="food-card-desc">${itemDesc}</p>
          <div class="food-card-footer">
            <div class="food-card-price-box">
              <span class="currency-symbol">₹</span>
              <span class="food-card-price">${item.price.toFixed(2)}</span>
            </div>
            <div class="food-card-action">
              ${actionHtml}
            </div>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function updateItemQuantity(itemId, delta) {
  const current = state.cart[itemId] || 0;
  const updated = current + delta;

  if (updated <= 0) {
    delete state.cart[itemId];
  } else {
    state.cart[itemId] = updated;
  }

  renderMenu();
  const activeView = document.querySelector('.view-section.active');
  if (activeView && activeView.id === 'view-cart') {
    renderCart();
  }
}

// Cart View
function renderCart() {
  const container = document.getElementById('cart-items-container');
  const checkoutBtn = document.getElementById('cart-checkout-btn');
  const totalAmountEl = document.getElementById('cart-grand-total');

  const cartEntries = Object.entries(state.cart);

  if (cartEntries.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 48px 16px; color: var(--text-muted);">
        <div style="font-size: 2.5rem; margin-bottom: 12px;">🛒</div>
        <h3>Your food cart is empty</h3>
        <p style="margin-top: 6px;">Add delicious food items from the menu to proceed.</p>
        <button class="btn btn-primary" style="margin-top: 18px;" onclick="showView('menu')">Browse Food Menu →</button>
      </div>
    `;
    if (checkoutBtn) checkoutBtn.disabled = true;
    if (totalAmountEl) totalAmountEl.textContent = '₹0.00';
    return;
  }

  let grandTotal = 0;
  let rowsHtml = '';

  cartEntries.forEach(([itemIdStr, qty]) => {
    const itemId = parseInt(itemIdStr, 10);
    const item = state.foodItems.find(x => x.id === itemId);
    if (!item) return;

    const subtotal = item.price * qty;
    grandTotal += subtotal;

    rowsHtml += `
      <tr>
        <td>
          <div style="font-weight: 700; color: var(--text-main);">${item.name}</div>
          <div style="font-size: 0.8rem; color: var(--text-muted);">${item.category || 'Canteen Special'}</div>
        </td>
        <td style="font-weight: 600;">₹${item.price.toFixed(2)}</td>
        <td>
          <div class="card-qty-control" style="display: inline-flex;">
            <button class="card-qty-btn btn-minus" onclick="updateItemQuantity(${item.id}, -1)">−</button>
            <span class="card-qty-value">${qty}</span>
            <button class="card-qty-btn btn-plus" onclick="updateItemQuantity(${item.id}, 1)">+</button>
          </div>
        </td>
        <td style="font-weight: 700; text-align: right;">₹${subtotal.toFixed(2)}</td>
      </tr>
    `;
  });

  container.innerHTML = `
    <table class="cart-items-table">
      <thead>
        <tr>
          <th>Food Item</th>
          <th>Price</th>
          <th>Quantity</th>
          <th style="text-align: right;">Subtotal</th>
        </tr>
      </thead>
      <tbody>
        ${rowsHtml}
      </tbody>
    </table>
  `;

  if (totalAmountEl) totalAmountEl.textContent = `₹${grandTotal.toFixed(2)}`;
  if (checkoutBtn) checkoutBtn.disabled = false;
}

// Payment View
function renderPaymentView() {
  const total = getCartTotalPrice();
  const el = document.getElementById('payment-order-amount');
  if (el) el.textContent = `₹${total.toFixed(2)}`;
  selectPaymentMethod(state.selectedPaymentMethod);
}

function selectPaymentMethod(method) {
  state.selectedPaymentMethod = method;
  document.querySelectorAll('.payment-method-card').forEach(c => {
    c.classList.toggle('active', c.dataset.method === method);
  });
}

function togglePaymentSim(checkbox) {
  state.simulatePaymentSuccess = checkbox.checked;
}

function processPayment() {
  const total = getCartTotalPrice();
  if (total <= 0) {
    showToast('Your cart is empty.', 'error');
    showView('menu');
    return;
  }

  const payBtn = document.getElementById('btn-confirm-payment');
  if (payBtn) {
    payBtn.disabled = true;
    payBtn.textContent = 'Processing Payment & Generating Token...';
  }

  setTimeout(() => {
    if (payBtn) {
      payBtn.disabled = false;
      payBtn.textContent = 'Pay Now & Generate Token →';
    }

    if (!state.simulatePaymentSuccess) {
      showToast('Payment failed: Bank simulator returned decline. Try again.', 'error');
      return;
    }

    // Payment Successful -> Generate Live Order with Instant Token
    const tokenNum = state.tokenCounter++;
    const now = new Date();
    const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    const orderItems = Object.entries(state.cart).map(([itemIdStr, qty]) => {
      const item = state.foodItems.find(x => x.id === parseInt(itemIdStr, 10));
      return {
        id: item.id,
        name: item.name,
        price: item.price,
        quantity: qty,
        subtotal: item.price * qty
      };
    });

    const newOrder = {
      id: tokenNum,
      tokenNumber: tokenNum,
      studentName: state.currentUser ? state.currentUser.fullName : 'Guest Student',
      studentRoll: state.currentUser ? (state.currentUser.rollNumber || 'N/A') : 'N/A',
      items: orderItems,
      amount: total,
      paymentMethod: state.selectedPaymentMethod,
      paymentStatus: 'SUCCESS',
      status: 'QUEUED',
      orderTime: timeStr
    };

    state.orders.unshift(newOrder);
    state.activeOrder = newOrder;
    state.cart = {}; // Clear cart upon successful order
    saveState();

    showToast(`Payment successful! Token #${tokenNum} issued.`, 'success');
    showView('token-status');
  }, 900);
}

// Token Status View
function renderTokenStatus() {
  const order = state.activeOrder;
  if (!order) {
    showToast('No active order token to display.', 'info');
    showView('menu');
    return;
  }

  const tokenBadge = document.getElementById('token-number-badge');
  const studentNameEl = document.getElementById('token-student-name');
  const tokenTimeEl = document.getElementById('token-order-time');
  const tokenAmtEl = document.getElementById('token-order-amount');
  const tokenMethodEl = document.getElementById('token-order-method');
  const tokenItemsList = document.getElementById('token-items-summary');

  if (tokenBadge) tokenBadge.textContent = `#${order.tokenNumber}`;
  if (studentNameEl) studentNameEl.textContent = `${order.studentName} (${order.studentRoll})`;
  if (tokenTimeEl) tokenTimeEl.textContent = order.orderTime;
  if (tokenAmtEl) tokenAmtEl.textContent = `₹${order.amount.toFixed(2)}`;
  if (tokenMethodEl) tokenMethodEl.textContent = order.paymentMethod;

  if (tokenItemsList) {
    tokenItemsList.innerHTML = order.items.map(i => `
      <div style="display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px dashed var(--border);">
        <span>${i.name} × ${i.quantity}</span>
        <span style="font-weight: 600;">₹${i.subtotal.toFixed(2)}</span>
      </div>
    `).join('');
  }

  updateStepper(order.status);
}

function updateStepper(status) {
  const steps = ['QUEUED', 'PREPARING', 'READY', 'SERVED'];
  const currentIndex = steps.indexOf(status);

  steps.forEach((s, idx) => {
    const el = document.getElementById(`step-${s.toLowerCase()}`);
    if (!el) return;

    el.classList.remove('active', 'completed');
    if (idx < currentIndex) {
      el.classList.add('completed');
    } else if (idx === currentIndex) {
      el.classList.add('active');
    }
  });

  const banner = document.getElementById('token-status-banner');
  if (banner) {
    if (status === 'QUEUED') {
      banner.innerHTML = `<span class="badge badge-warning">Order Placed (Queued)</span> — Your order is waiting to be accepted in the kitchen.`;
    } else if (status === 'PREPARING') {
      banner.innerHTML = `<span class="badge badge-primary">Preparing Meal</span> — Chef is preparing your delicious dishes fresh.`;
    } else if (status === 'READY') {
      banner.innerHTML = `<span class="badge badge-success">🎉 Order Ready for Pickup!</span> — Please proceed to the canteen counter with Token <strong>#${state.activeOrder.tokenNumber}</strong>.`;
    } else if (status === 'SERVED') {
      banner.innerHTML = `<span class="badge badge-dark">Served / Completed</span> — Thank you for ordering with SmartServe!`;
    }
  }
}

// Admin Dashboard Functions
function renderAdminDashboard() {
  const totalOrders = state.orders.length;
  const queuedCount = state.orders.filter(o => o.status === 'QUEUED').length;
  const preparingCount = state.orders.filter(o => o.status === 'PREPARING').length;
  const readyCount = state.orders.filter(o => o.status === 'READY').length;
  const servedCount = state.orders.filter(o => o.status === 'SERVED').length;
  const totalRevenue = state.orders.reduce((sum, o) => sum + (o.paymentStatus === 'SUCCESS' ? o.amount : 0), 0);

  const kpiTotal = document.getElementById('admin-kpi-total');
  const kpiQueued = document.getElementById('admin-kpi-queued');
  const kpiPreparing = document.getElementById('admin-kpi-preparing');
  const kpiReady = document.getElementById('admin-kpi-ready');
  const kpiRevenue = document.getElementById('admin-kpi-revenue');

  if (kpiTotal) kpiTotal.textContent = totalOrders;
  if (kpiQueued) kpiQueued.textContent = queuedCount;
  if (kpiPreparing) kpiPreparing.textContent = preparingCount;
  if (kpiReady) kpiReady.textContent = readyCount;
  if (kpiRevenue) kpiRevenue.textContent = `₹${totalRevenue.toFixed(0)}`;

  renderAdminOrdersTable();
}

function renderAdminOrdersTable() {
  const tbody = document.getElementById('admin-orders-tbody');
  if (!tbody) return;

  if (state.orders.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 32px; color: var(--text-muted);">No orders recorded yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = state.orders.map(order => {
    let statusBadge = '';
    if (order.status === 'QUEUED') statusBadge = '<span class="badge badge-warning">QUEUED</span>';
    else if (order.status === 'PREPARING') statusBadge = '<span class="badge badge-primary">PREPARING</span>';
    else if (order.status === 'READY') statusBadge = '<span class="badge badge-success">READY</span>';
    else if (order.status === 'SERVED') statusBadge = '<span class="badge badge-dark">SERVED</span>';

    const itemsSummary = order.items.map(i => `${i.name} (${i.quantity})`).join(', ');

    return `
      <tr>
        <td style="font-weight: 800; color: var(--primary);">#${order.tokenNumber}</td>
        <td>
          <div style="font-weight: 600;">${order.studentName}</div>
          <div style="font-size: 0.78rem; color: var(--text-muted);">${order.studentRoll}</div>
        </td>
        <td style="font-size: 0.85rem; max-width: 200px;">${itemsSummary}</td>
        <td style="font-weight: 700;">₹${order.amount.toFixed(2)}</td>
        <td><span class="badge badge-success">${order.paymentMethod}</span></td>
        <td>${statusBadge}</td>
        <td>
          <select class="form-control" style="padding: 4px 8px; font-size: 0.82rem; width: auto;" onchange="updateOrderStatus(${order.id}, this.value)">
            <option value="QUEUED" ${order.status === 'QUEUED' ? 'selected' : ''}>QUEUED</option>
            <option value="PREPARING" ${order.status === 'PREPARING' ? 'selected' : ''}>PREPARING</option>
            <option value="READY" ${order.status === 'READY' ? 'selected' : ''}>READY</option>
            <option value="SERVED" ${order.status === 'SERVED' ? 'selected' : ''}>SERVED</option>
          </select>
        </td>
      </tr>
    `;
  }).join('');
}

function updateOrderStatus(orderId, newStatus) {
  const order = state.orders.find(o => o.id === orderId);
  if (!order) return;

  order.status = newStatus;
  saveState();
  showToast(`Token #${order.tokenNumber} status changed to ${newStatus}`, 'info');

  if (state.activeOrder && state.activeOrder.id === orderId) {
    state.activeOrder.status = newStatus;
    const activeView = document.querySelector('.view-section.active');
    if (activeView && activeView.id === 'view-token-status') {
      renderTokenStatus();
    }
  }

  renderAdminDashboard();
}

// Live Orders Feed for Campus Display Board
function renderLiveOrders() {
  const readyGrid = document.getElementById('live-ready-tokens');
  const prepGrid = document.getElementById('live-prep-tokens');

  const readyOrders = state.orders.filter(o => o.status === 'READY');
  const prepOrders = state.orders.filter(o => o.status === 'PREPARING' || o.status === 'QUEUED');

  if (readyGrid) {
    if (readyOrders.length === 0) {
      readyGrid.innerHTML = `<div style="color: var(--text-muted); padding: 12px;">No tokens ready for collection yet.</div>`;
    } else {
      readyGrid.innerHTML = readyOrders.map(o => `
        <div class="live-token-pill ready-token">
          <div class="token-num">#${o.tokenNumber}</div>
          <div class="student-name">${o.studentName}</div>
        </div>
      `).join('');
    }
  }

  if (prepGrid) {
    if (prepOrders.length === 0) {
      prepGrid.innerHTML = `<div style="color: var(--text-muted); padding: 12px;">Kitchen queue is clear.</div>`;
    } else {
      prepGrid.innerHTML = prepOrders.map(o => `
        <div class="live-token-pill prep-token">
          <div class="token-num">#${o.tokenNumber}</div>
          <div class="student-name">${o.studentName} (${o.status})</div>
        </div>
      `).join('');
    }
  }
}

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
  // Check if admin or student was previously remembered or start at login
  showView('login');
});
