const API_BASE_URL = 'http://localhost:8080';

const orgInput = document.getElementById('org-input');
const limitInput = document.getElementById('limit-input');
const loadButton = document.getElementById('load-button');
const resultsContainer = document.getElementById('results-container');
const decrementBtn = document.getElementById('decrement-btn');
const incrementBtn = document.getElementById('increment-btn');

const sortSelect = document.getElementById('sort-select');
const selectWrapper = document.querySelector('.custom-select-wrapper');
const selectTrigger = document.querySelector('.custom-select-trigger');
const customOptions = document.querySelectorAll('.custom-option');

orgInput.addEventListener('input', () => {
    if (orgInput.value.trim() !== '') {
        orgInput.classList.remove('input-error');
    }
});

selectTrigger.addEventListener('click', () => {
    selectWrapper.classList.toggle('open');
});

customOptions.forEach(option => {
    option.addEventListener('click', () => {
        customOptions.forEach(o => o.classList.remove('selected'));
        option.classList.add('selected');

        const selectedValue = option.getAttribute('data-value');
        const selectedText = option.textContent;

        if (sortSelect instanceof HTMLSelectElement) {
            sortSelect.value = selectedValue;
        }

        selectTrigger.querySelector('span').textContent = selectedText;
        selectWrapper.classList.remove('open');
    });
});

window.addEventListener('click', (e) => {
    if (!selectWrapper.contains(e.target)) {
        selectWrapper.classList.remove('open');
    }
});

function updateLimit(amount) {
    if (!(limitInput instanceof HTMLInputElement)) return;

    const min = parseInt(limitInput.min, 10);
    const max = parseInt(limitInput.max, 10);
    let currentValue = parseInt(limitInput.value, 10) || min;
    let newValue = currentValue + amount;

    if (newValue < min) newValue = min;
    if (newValue > max) newValue = max;

    limitInput.value = newValue.toString();
}

decrementBtn.addEventListener('click', () => updateLimit(-1));
incrementBtn.addEventListener('click', () => updateLimit(1));
loadButton.addEventListener('click', fetchAndDisplayRepos);

async function fetchAndDisplayRepos() {
    if (limitInput instanceof HTMLInputElement) {
        const min = parseInt(limitInput.min, 10);
        const max = parseInt(limitInput.max, 10);
        let value = parseInt(limitInput.value, 10);
        if (isNaN(value) || value < min) value = min;
        if (value > max) value = max;
        limitInput.value = value.toString();
    }

    const org = orgInput.value.trim();
    const sort = sortSelect.value;
    const limit = limitInput.value;

    if (!org) {
        orgInput.classList.add('input-error');
        return;
    } else {
        orgInput.classList.remove('input-error');
    }

    loadButton.disabled = true;
    resultsContainer.innerHTML = `<p>Loading...</p>`;

    try {
        const url = `${API_BASE_URL}/api/org/${org}/repos?limit=${limit}&sort=${sort}`;
        const response = await fetch(url);

        if (!response.ok) {
            const errorMessage = `Network error! Status: ${response.status}`;
            console.error(errorMessage);
            resultsContainer.innerHTML = `<p style="color:red;">Error: ${errorMessage}</p>`;
            return;
        }

        const repos = await response.json();

        if (repos.length === 0) {
            resultsContainer.innerHTML = `<p>No repositories found for "${org}"</p>`;
            return;
        }

        resultsContainer.innerHTML = repos.map(r => `
            <div class="repo-card">
                <h3><a href="${r.html_url}" target="_blank">${r.name}</a></h3>
                <p class="stats">
                    Stars: ${r.stargazers_count} | Forks: ${r.forks_count} | Language: ${r.language || 'N/A'}
                </p>
                <p class="updated-date">Updated: ${new Date(r.updated_at).toLocaleString()}</p>
                ${r.description ? `<p class="description">${r.description}</p>` : ''}
            </div>
        `).join('');

    } catch (error) {
        console.error(error);
        resultsContainer.innerHTML = `<p style="color:red;">Error: ${error.message}</p>`;
    } finally {
        loadButton.disabled = false;
    }
}