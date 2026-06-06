// The Base URL for your Spring Boot REST API
const API_BASE_URL = "/api";

// --- Global State ---
// Track the current user (Loaded from local storage on refresh)
let currentUser = localStorage.getItem("railwayUser");
let userRole = localStorage.getItem("railwayUserRole"); // Tracks user role (admin/passenger)
let currentBookingTrain = null;
const SAMPLE_PNRS = ['PNR001', 'PNR002', 'PNR003', 'PNR004', 'PNR005'];

// --- Utility Functions ---

function updateNavbar() {
    const loginBtn = document.getElementById("login-btn");
    const logoutBtn = document.getElementById("logout-btn");
    const adminLink = document.getElementById("admin-link");
    
    // Check local storage for persistent state
    currentUser = localStorage.getItem("railwayUser");
    userRole = localStorage.getItem("railwayUserRole");

    if (currentUser) {
        if(loginBtn) loginBtn.style.display = "none";
        if(logoutBtn) logoutBtn.style.display = "inline-block";
        
        // Secure Admin link visibility
        if(adminLink) {
            adminLink.style.display = userRole === 'admin' ? "inline-block" : "none";
        }

    } else {
        if(loginBtn) loginBtn.style.display = "inline-block";
        if(logoutBtn) logoutBtn.style.display = "none";
        if(adminLink) adminLink.style.display = "none";
    }
}

// Redirects and checks if user is still logged in
function logout() {
    currentUser = null;
    userRole = null;
    localStorage.clear(); // Clear all user-specific data
    window.location.href = 'login.html';
}

// --- Modals / Context ---

function closeBookingModal() {
    document.getElementById('booking-modal').style.display = 'none';
}
function openCancelModal(pnr) {
    document.getElementById('cancel-pnr-display').innerText = pnr;
    document.getElementById('pnr-to-cancel').value = pnr;
    document.getElementById('cancel-modal').style.display = 'block';
}
function closeCancelModal() {
    document.getElementById('cancel-modal').style.display = 'none';
}
function closeResponseModal() {
    document.getElementById('response-modal').style.display = 'none';
    if (window.location.pathname.endsWith('admin_feedback.html')) {
        loadAdminFeedback(); // Refresh list only on the admin page
    }
}

window.onclick = function(event) {
    // Close any modal when clicking outside the modal content
    if (event.target.classList.contains('modal')) {
        event.target.style.display = "none";
    }
};

/**
 * Executes on index.html load to check if user was redirected mid-booking.
 */
function checkBookingContext() {
    const context = localStorage.getItem('bookingContext');
    
    // Check if we were redirected to index.html with a booking intention
    if (window.location.hash === '#book' && context) {
        const { trainNumber, trainName, price, date, source, destination } = JSON.parse(context);
        
        // Clear context to prevent repeated popups
        localStorage.removeItem('bookingContext'); 
        
        // Set search fields from context (for visual consistency)
        document.getElementById('source').value = source;
        document.getElementById('destination').value = destination;
        document.getElementById('date').value = date;
        
        // Open the booking modal with the correct train data
        openBookingModalWithContext(trainNumber, trainName, price, date);
    }
}

/**
 * Helper function to open the booking modal and set context.
 * @param {string} trainNumber 
 * @param {string} trainName 
 * @param {string} price 
 * @param {string} date 
 */
function openBookingModalWithContext(trainNumber, trainName, price, date) {
    currentBookingTrain = { trainNumber, trainName, price, date };
    const summary = document.getElementById('booking-summary');
    
    summary.innerHTML = `<strong>${trainName} (${trainNumber})</strong><br>Date: ${date}<br>Price: $${price}`;
    
    document.getElementById('booking-modal').style.display = 'block';
}


// --- AUTHENTICATION / REGISTRATION ---

/**
 * Handles successful authentication and fetches user data to determine role and profile display.
 * @param {Event} event 
 */
async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
             alert("Login Failed: Invalid username or password.");
             return;
        }

        const json = await response.json();
        
        // CRITICAL: The API must return the 'data' (User object) which contains the 'role'
        if (json.success && json.data && json.data.role) {
            currentUser = username;
            userRole = json.data.role.toLowerCase();
            
            // Save data for page transitions
            localStorage.setItem("railwayUser", currentUser);
            localStorage.setItem("railwayUserRole", userRole);
            localStorage.setItem("railuserName", json.data.name); 

            // --- Context Check (Did user try to book before logging in?) ---
            const context = localStorage.getItem('bookingContext');
            if (context) {
                localStorage.removeItem('bookingContext');
                // Redirect back to the index page to open the booking modal
                window.location.href = 'index.html#book';
            } else {
                // Determine hub based on role (THIS IS THE CORRECTED LOGIC)
                window.location.href = userRole === 'admin' ? 'admin_hub.html' : 'passenger_dashboard.html'; 
            }
        } else {
            alert("Login Failed: Server authentication error or role data missing.");
        }
    } catch (error) {
        alert("Network Error: Could not connect to API.");
    }
}

async function handleRegistration(event) {
    event.preventDefault();
    const requestBody = {
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value,
        name: document.getElementById('reg-name').value,
        address: document.getElementById('reg-address').value,
        city: document.getElementById('reg-city').value,
        age: parseInt(document.getElementById('reg-age').value),
        contact: document.getElementById('reg-contact').value,
        gender: document.getElementById('reg-gender').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            alert(`Registration successful for ${requestBody.username}. Please log in now.`);
            window.location.href = 'login.html';
        } else {
            const errorJson = await response.json();
            alert("Registration Failed: " + (errorJson.error || "Server error. Username may already exist."));
        }
    } catch (error) {
        alert("Registration Network Error: " + error.message);
    }
}

// --- DASHBOARD / PROFILE ---

/**
 * Loads user data into the profile page form.
 * Handles both GET (fetch data) and PUT (update data) operations.
 */
async function loadUserProfile(username) {
    const loadingDiv = document.getElementById('profile-loading');
    const formSection = document.getElementById('profile-details-section');

    if (!loadingDiv || !formSection) return;

    loadingDiv.style.display = 'block';
    formSection.style.display = 'none';

    try {
        const response = await fetch(`${API_BASE_URL}/users/${username}`);
        if (!response.ok) throw new Error('Failed to fetch profile details.');

        const json = await response.json();
        const user = json.data;

        // Pre-fill form fields
        document.getElementById('profile-username').value = user.username;
        document.getElementById('profile-name').value = user.name;
        document.getElementById('profile-address').value = user.address || '';
        document.getElementById('profile-city').value = user.city;
        document.getElementById('profile-contact').value = user.contact;
        document.getElementById('profile-age').value = user.age || 0;
        document.getElementById('profile-gender').value = user.gender;
        
        document.getElementById('profile-role').innerText = user.role.toUpperCase();

        loadingDiv.style.display = 'none';
        formSection.style.display = 'block';

    } catch (error) {
        loadingDiv.innerHTML = `<p style="color:red; text-align:center">Error: ${error.message}</p>`;
    }
}

async function handleProfileUpdate(event) {
    event.preventDefault();
    const username = document.getElementById('profile-username').value;
    
    const requestBody = {
        username: username,
        name: document.getElementById('profile-name').value,
        address: document.getElementById('profile-address').value,
        city: document.getElementById('profile-city').value,
        age: parseInt(document.getElementById('profile-age').value),
        contact: document.getElementById('profile-contact').value,
        gender: document.getElementById('profile-gender').value,
        role: userRole // Send back the existing role for persistence
    };

    try {
        const response = await fetch(`${API_BASE_URL}/users/${username}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            alert("Profile updated successfully!");
            // Update local storage name for dashboard display
            localStorage.setItem("railuserName", requestBody.name); 
            // Re-run load to confirm changes and update dashboard display
            window.location.href = 'passenger_dashboard.html'; 
        } else {
            const errorJson = await response.json();
            alert("Update Failed: " + (errorJson.error || "Server error. Check Java console."));
        }
    } catch (error) {
        alert("Network Error during profile update: " + error.message);
    }
}


// --- SEARCH / BOOKING ---

async function searchTrains(event) {
    event.preventDefault();
    const source = document.getElementById('source').value;
    const destination = document.getElementById('destination').value;
    const date = document.getElementById('date').value;
    const resultsArea = document.getElementById('results-area');

    resultsArea.innerHTML = '<p style="text-align:center">Searching...</p>';

    try {
        const params = new URLSearchParams({ source, destination, date });
        const response = await fetch(`${API_BASE_URL}/trains/search?${params.toString()}`);
        
        if (!response.ok) throw new Error('Search failed');
        
        const json = await response.json();
        const trains = json.data ? json.data : json;

        resultsArea.innerHTML = '';
        if (!trains || trains.length === 0) {
            resultsArea.innerHTML = '<p style="text-align:center">No trains found.</p>';
            return;
        }

        trains.forEach(train => {
            const card = document.createElement('div');
            card.className = 'train-card';
            
            // Assume 100 total seats, check logic in Java to calculate availableSeats
            const seats = train.availableSeats !== null ? train.availableSeats : (train.totalSeats || 100); 
            const statusClass = seats <= 5 ? 'status-sold-out' : 'status-available'; // Highlight low seats
            const buttonText = seats === 0 ? 'Sold Out' : 'Book';

            card.innerHTML = `
                <div class="train-info">
                    <h3>${train.trainName} <small>(${train.trainNumber})</small></h3>
                    <div class="train-route">${train.source} ➔ ${train.destination}</div>
                    <div class="train-time">Date: ${train.date}</div>
                </div>
                <div class="train-action">
                    <div class="train-price">$${train.cost}</div>
                    <div class="${statusClass}"><small>${seats} seats left</small></div>
                    <button class="btn-primary" 
                        ${seats === 0 ? 'disabled' : ''}
                        onclick="initiateBooking('${train.trainNumber}', '${train.trainName}', '${train.cost}', '${train.date}')">
                        ${buttonText}
                    </button>
                </div>
            `;
            resultsArea.appendChild(card);
        });

    } catch (error) {
        resultsArea.innerHTML = `<p style="color:red; text-align:center">Error: ${error.message}</p>`;
    }
}


/**
 * Initiates the booking flow, checking for login status and saving context if necessary.
 * @param {string} trainNumber 
 * @param {string} trainName 
 * @param {string} price 
 * @param {string} date 
 */
function initiateBooking(trainNumber, trainName, price, date) {
    // Read search input values (source/destination) immediately before redirection
    const sourceElement = document.getElementById('source');
    const destinationElement = document.getElementById('destination');

    const source = sourceElement ? sourceElement.value : '';
    const destination = destinationElement ? destinationElement.value : '';

    if (!currentUser) {
        // Save the booking context before redirecting to login
        localStorage.setItem('bookingContext', JSON.stringify({
            trainNumber,
            trainName,
            price,
            date,
            source, // Save context
            destination // Save context
        }));
        
        // Redirect to login page
        window.location.href = 'login.html'; 
        return;
    }
    
    // If logged in, open modal immediately
    openBookingModalWithContext(trainNumber, trainName, price, date);
}

async function handleBooking(event) {
    event.preventDefault();
    if(!currentBookingTrain) return;

    const bookingData = {
        username: currentUser,
        trainNumber: currentBookingTrain.trainNumber,
        passengerName: document.getElementById('passenger-name').value,
        age: parseInt(document.getElementById('passenger-age').value),
        gender: document.getElementById('passenger-gender').value
        // Route data removed from payload (Normalization Fix)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bookingData)
        });

        if (response.ok) {
            alert("Booking Confirmed! 🎫");
            closeBookingModal();
        } else {
            const errorText = await response.text();
            alert("Booking Failed: " + errorText);
        }
    } catch (error) {
        alert("Network Error: " + error.message);
    }
}

// --- CANCELLATION ---

async function confirmCancellation() {
    const pnr = document.getElementById('pnr-to-cancel').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/${pnr}?username=${currentUser}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert(`Cancellation successful for PNR: ${pnr}.`);
            closeCancelModal();
            // Refresh the list immediately if on the bookings page
            if (window.location.pathname.endsWith('bookings.html')) {
                loadMyBookings();
            }
        } else {
            const errorText = await response.text();
            alert("Cancellation Failed: " + errorText);
        }
    } catch (error) {
        alert(`Network Error during cancellation: ${error.message}`);
    }
}


// --- PASSENGER VIEW (BOOKINGS.HTML) ---

async function loadMyBookings() {
    if (!currentUser) return; 
    
    const list = document.getElementById('bookings-list-container');
    if (!list) return;

    list.innerHTML = '<p style="text-align:center">Loading your tickets...</p>';
    
    try {
        // CRITICAL: Check role to decide endpoint
        // Admins call the generic GET /api/bookings (unfiltered)
        const endpoint = userRole === 'admin' ? `${API_BASE_URL}/bookings` : `${API_BASE_URL}/bookings/user/${currentUser}`;
        
        const response = await fetch(endpoint);
        const json = await response.json();
        const allBookings = json.data ? json.data : json;

        // Final filtering to hide corrupted/sample data
        const myBookings = allBookings.filter(b => 
            !SAMPLE_PNRS.includes(b.pnr) && b.date && b.source // Checks for corruption/samples
        );
        
        if (!myBookings || myBookings.length === 0) {
            list.innerHTML = '<p style="text-align:center">You have no active bookings.</p>';
            return;
        }

        list.innerHTML = myBookings.map(b => `
            <div class="booking-card">
                <div class="booking-details">
                    <h3>PNR: <span style="color:var(--primary)">${b.pnr}</span></h3>
                    <p>Route: ${b.source} ➔ ${b.destination}</p>
                    <p>Travel Date: ${b.date} <span style="margin-left:10px; color:#666">(${b.trainNumber})</span></p>
                </div>
                <div class="booking-actions">
                    <div style="font-weight:bold; color:${b.status === 'CANCELLED' ? 'var(--accent)' : 'green'}">
                        ${b.status || 'CONFIRMED'}
                    </div>
                    <button class="btn-cancel" onclick="openCancelModal('${b.pnr}')" 
                        ${b.status === 'CANCELLED' ? 'disabled' : ''}>
                        ${b.status === 'CANCELLED' ? 'Cancelled' : 'Cancel Ticket'}
                    </button>
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        console.error(error);
        list.innerHTML = '<p style="text-align:center; color:red">Failed to load bookings. Ensure Java Backend is running.</p>';
    }
}


// --- ADMIN FUNCTIONS ---

function checkAdminAccess() {
    const authCheckDiv = document.getElementById('admin-auth-check');
    const adminSidebar = document.getElementById('admin-sidebar');
    const adminMainContent = document.getElementById('admin-main-content');
    
    // Check if we are on a page that doesn't require hard checks (e.g., index/bookings)
    if (!authCheckDiv) return true; 

    if (!currentUser) {
        authCheckDiv.innerHTML = `<div class="card" style="text-align: center; color: var(--accent); margin-top: 50px;"><h2>Access Denied</h2><p>Please log in.</p><a href="login.html" class="btn-primary" style="margin-top: 20px;">Log In Now</a></div>`;
        return false;
    }
    
    if (userRole !== 'admin') {
        authCheckDiv.innerHTML = `<div class="card" style="text-align: center; color: var(--accent); margin-top: 50px;"><h2>Access Denied</h2><p>You do not have administrator privileges.</p><a href="passenger_dashboard.html" class="btn-primary" style="margin-top: 20px;">Go to Dashboard</a></div>`;
        return false;
    }
    
    // If successful, hide check div and show content divs
    if (authCheckDiv) authCheckDiv.style.display = 'none';
    
    // NOTE: Specific pages (like admin_hub) use the sidebar/main content directly.
    if (adminSidebar) adminSidebar.style.display = 'block';
    if (adminMainContent) adminMainContent.style.display = 'block';
    
    return true;
}


async function loadAdminTrains() {
    // Logic for loading the train table on admin.html
    const container = document.getElementById('admin-content-wrapper');
    if (!container) return; 

    // 1. Build the HTML structure
    container.innerHTML = `
        <section id="admin-section">
            <div class="admin-header">
                <h2><i class="fa-solid fa-screwdriver-wrench"></i> Manage Train Services</h2>
                <a href="add_train.html" class="btn-primary" style="width: auto; padding: 10px 20px; font-size: 0.95rem;">
                    <i class="fa-solid fa-plus"></i> Add New Train
                </a>
            </div>
            
            <div id="admin-train-list-container" class="card" style="padding: 25px;">
                <p style="text-align: center; color: var(--text-light)"><i class="fa-solid fa-spinner fa-spin"></i> Loading train inventory...</p>
            </div>
        </section>`;
    
    const listContainer = document.getElementById('admin-train-list-container');

    // 2. Fetch the data
    try {
        const response = await fetch(`${API_BASE_URL}/trains`);
        const json = await response.json();
        const trains = json.data || [];

        if (trains.length === 0) {
            listContainer.innerHTML = '<p style="text-align:center">No trains registered in the system.</p>';
            return;
        }

        let tableHtml = `<table class="train-table">
                            <thead>
                                <tr>
                                    <th>No.</th>
                                    <th>Name / ID</th>
                                    <th>Route</th>
                                    <th>Date</th>
                                    <th>Cost</th>
                                    <th>Seats</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>`;
        
        trains.forEach((train) => {
            const seats = train.availableSeats !== null ? train.availableSeats : (train.totalSeats || 100);
            tableHtml += `
                <tr>
                    <td>${train.trainNumber}</td>
                    <td>${train.trainName}</td>
                    <td>${train.source} → ${train.destination}</td>
                    <td>${train.date}</td>
                    <td>$${train.cost}</td>
                    <td>${seats}</td>
                    <td class="admin-action-btns">
                        <a href="edit_train.html?train=${train.trainNumber}" class="btn-secondary" style="padding: 6px 10px;">Edit</a>
                        <button class="btn-delete" onclick="handleDeleteTrain('${train.trainNumber}')">Delete</button>
                    </td>
                </tr>
            `;
        });

        tableHtml += `</tbody></table>`;
        listContainer.innerHTML = tableHtml;

    } catch (error) {
        listContainer.innerHTML = `<p style="color:red; text-align:center">Error loading trains: ${error.message}</p>`;
    }
}

async function handleDeleteTrain(trainNumber) {
    if (!confirm(`Are you sure you want to delete train ${trainNumber}? This cannot be undone.`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/trains/${trainNumber}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert(`Train ${trainNumber} deleted successfully.`);
            loadAdminTrains(); // Refresh the list
        } else {
            const errorText = await response.text();
            alert(`Deletion failed: ${errorText}`);
        }
    } catch (error) {
        alert(`Network error during deletion: ${error.message}`);
    }
}

async function loadTrainForEditing(trainNumber) {
    // Logic for loading train data into the edit_train.html form
    document.getElementById('loading-indicator').style.display = 'block';
    document.getElementById('edit-train-section').style.display = 'none';

    try {
        const response = await fetch(`${API_BASE_URL}/trains/${trainNumber}`);
        if (!response.ok) throw new Error("Could not find train details.");
        
        const json = await response.json();
        const train = json.data;

        // Pre-fill the form fields
        document.getElementById('admin-train-number').value = train.trainNumber;
        document.getElementById('admin-train-name').value = train.trainName;
        document.getElementById('admin-source').value = train.source;
        document.getElementById('admin-destination').value = train.destination;
        document.getElementById('admin-date').value = train.date;
        document.getElementById('admin-cost').value = train.cost;
        document.getElementById('admin-total-seats').value = train.totalSeats || 100; // FIX: Added seats
        
        document.getElementById('loading-indicator').style.display = 'none';
        document.getElementById('edit-train-section').style.display = 'block';

    } catch (error) {
        document.getElementById('loading-indicator').style.display = 'none';
        document.getElementById('edit-train-section').innerHTML = `<div class="card"><h2>Error</h2><p style="color:red;">Failed to load train details: ${error.message}</p></div>`;
    }
}

async function handleTrainSubmission(event) {
    // Logic for POST (Add) or PUT (Edit) for admin forms
    event.preventDefault();
    
    const mode = document.getElementById('train-mode').value;
    const trainNumber = document.getElementById('admin-train-number').value;

    const trainData = {
        trainNumber: trainNumber,
        trainName: document.getElementById('admin-train-name').value,
        source: document.getElementById('admin-source').value,
        destination: document.getElementById('admin-destination').value,
        date: document.getElementById('admin-date').value,
        cost: parseFloat(document.getElementById('admin-cost').value),
        totalSeats: parseInt(document.getElementById('admin-total-seats').value) // FIX: Added seats submission
    };

    try {
        const endpoint = `${API_BASE_URL}/trains${mode === 'edit' ? '/' + trainNumber : ''}`;
        
        const response = await fetch(endpoint, {
            method: mode === 'add' ? 'POST' : 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(trainData)
        });

        if (response.ok) {
            alert(`Train ${mode === 'add' ? 'added' : 'updated'} successfully!`);
            window.location.href = 'admin.html'; // Redirect back to the view list
        } else {
            const errorJson = await response.json();
            alert(`${mode === 'add' ? 'Add' : 'Update'} Failed: ` + (errorJson.error || "Server error. Check Java console."));
        }
    } catch (error) {
        alert("Network Error during submission: " + error.message);
    }
}


// --- FEEDBACK FUNCTIONS ---

async function handleFeedbackSubmission(event) {
    // Logic for passenger submitting feedback
    event.preventDefault();

    const rating = document.getElementById('feedback-rating').value;
    if (rating === '0' || !rating) {
        alert("Please select a star rating before submitting.");
        return;
    }

    const feedbackData = {
        username: currentUser,
        trainNumber: document.getElementById('feedback-train-number').value,
        rating: parseInt(rating),
        comment: document.getElementById('feedback-comment').value
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/feedback`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(feedbackData)
        });

        if (response.ok) {
            alert("Feedback submitted successfully! Thank thank you.");
            document.getElementById('feedback-comment').value = ''; // Clear form
            document.getElementById('feedback-train-number').value = '';
            // Reset stars visually
            const stars = document.getElementById('star-rating-container').querySelectorAll('i');
            stars.forEach(star => { star.classList.remove('fa-solid', 'rated'); star.classList.add('fa-regular'); });
        } else {
            const errorJson = await response.json();
            alert("Submission Failed: " + (errorJson.error || "Server error. Check Java console."));
        }
    } catch (error) {
        alert("Network Error: Could not submit feedback.");
    }
}

let allFeedbackData = []; // Store all feedback for filtering

async function loadAdminFeedback() {
    // Logic for admin viewing all feedback
    const container = document.getElementById('feedback-list-container');
    if (!container) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/feedback`);
        const json = await response.json();
        allFeedbackData = json.data || [];
        
        // Calculate average rating
        const totalRating = allFeedbackData.reduce((sum, f) => sum + f.rating, 0);
        const avgRating = allFeedbackData.length > 0 ? (totalRating / allFeedbackData.length).toFixed(2) : 'N/A';
        document.getElementById('avg-rating').innerText = avgRating;

        filterFeedback(); // Initial load displays all
    } catch (error) {
        container.innerHTML = `<p style="color:red; text-align:center">Error loading feedback data: ${error.message}</p>`;
    }
}

function filterFeedback() {
    // Logic for filtering feedback list (All or Pending)
    const container = document.getElementById('feedback-list-container');
    const filterValue = document.getElementById('feedback-filter').value;
    
    const filteredList = allFeedbackData.filter(f => {
        const responded = f.adminResponse && f.adminResponse.trim() !== '';
        if (filterValue === 'PENDING') {
            return !responded;
        }
        return true; // ALL
    });
    
    if (filteredList.length === 0) {
        container.innerHTML = `<p style="text-align:center">${filterValue === 'PENDING' ? 'No pending feedback.' : 'No feedback found.'}</p>`;
        return;
    }
    
    container.innerHTML = filteredList.map(f => {
        const responded = f.adminResponse && f.adminResponse.trim() !== '';
        const statusText = responded ? 'Responded' : 'Pending';
        const statusColor = responded ? 'green' : 'var(--accent)';
        
        return `
            <div class="booking-card" style="margin-bottom: 20px; border-left-color: ${statusColor};">
                <div class="booking-details" style="flex-grow: 1;">
                    <h3 style="display: flex; justify-content: space-between;">
                        <span style="font-size: 1.2rem;">${f.trainNumber || 'General'} - ${f.username}</span>
                        <span style="font-size: 1.5rem; color: #ffc107;">
                            ${'★'.repeat(f.rating)}${'☆'.repeat(5 - f.rating)}
                        </span>
                    </h3>
                    <p style="margin-top: 5px;">Comments: ${f.comment}</p>
                    <p style="margin-top: 10px; font-style: italic; color: #555; border-top: 1px dashed #eee; padding-top: 5px;">
                        Admin Response: ${f.adminResponse || 'Awaiting response...'}
                    </p>
                </div>
                <div class="booking-actions">
                    <span style="color:${statusColor}; font-weight: 600;">${statusText}</span>
                    <button class="btn-primary" 
                        style="width: auto; padding: 8px 15px; font-size: 0.85rem; margin-top: 10px;"
                        onclick="openResponseModal(${f.feedbackId}, '${f.comment}')">
                        ${responded ? 'Edit Response' : 'Respond'}
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function openResponseModal(id, comment) {
    // Admin modal to submit a response
    document.getElementById('response-feedback-id').value = id;
    document.getElementById('admin-response-text').value = ''; // Clear previous text
    document.getElementById('feedback-details-summary').innerHTML = `
        <strong>ID: ${id}</strong><br>
        Comment: "${comment}"
    `;
    const modal = document.getElementById('response-modal');
    if (modal) modal.style.display = 'block';
}

async function handleAdminResponse(event) {
    // Logic for admin submitting the response text
    event.preventDefault();
    const feedbackId = document.getElementById('response-feedback-id').value;
    const adminResponse = document.getElementById('admin-response-text').value;

    try {
        const response = await fetch(`${API_BASE_URL}/feedback/${feedbackId}/respond`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ adminResponse })
        });
        
        if (response.ok) {
            alert("Response submitted successfully!");
            closeResponseModal();
        } else {
            const errorJson = await response.json();
            alert("Response Failed: " + (errorJson.error || "Server error. Check Java console."));
        }
    } catch (error) {
        alert("Network Error: Could not submit response.");
    }
}