/**
 * MoneyMate TypeScript Frontend - Modern UI Implementation
 *
 * OOP Concepts in TypeScript:
 * - Classes and Interfaces
 * - Encapsulation
 * - Type Safety
 * - Single Responsibility Principle
 */

const API_BASE_URL = 'http://localhost:8080/api';

// Interfaces (OOP Concept: Abstraction)
interface Transaction {
    transactionId: string;
    transactionType: string;
    amount: number;
    description: string;
    date: string;
    category: string;
    source?: string;
    paymentMethod?: string;
    isRecurring?: boolean;
}

interface Balance {
    totalBalance: number;
    totalIncome: number;
    totalExpense: number;
    initialBalance: number;
}

interface Category {
    name: string;
    displayName: string;
}

interface CategoryResponse {
    income: Category[];
    expense: Category[];
}

interface User {
    userId?: string;
    username: string;
    email: string;
    initialBalance?: number;
    createdAt?: string;
}

interface ActivityLog {
    logId: number;
    userId: string;
    activityType: string;
    description: string;
    timestamp: string;
}

/**
 * API Service Class (OOP Concept: Encapsulation)
 */
class MoneyMateAPI {
    private baseUrl: string;

    constructor(baseUrl: string) {
        this.baseUrl = baseUrl;
    }

    async getTransactions(): Promise<Transaction[]> {
        const response = await fetch(`${this.baseUrl}/transactions`);
        if (!response.ok) throw new Error('Failed to fetch transactions');
        return response.json();
    }

    async addTransaction(data: any): Promise<any> {
        const response = await fetch(`${this.baseUrl}/transactions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Failed to add transaction');
        }
        return response.json();
    }

    async deleteTransaction(id: string): Promise<any> {
        const response = await fetch(`${this.baseUrl}/transactions/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Failed to delete transaction');
        return response.json();
    }

    async getBalance(): Promise<Balance> {
        const response = await fetch(`${this.baseUrl}/balance`);
        if (!response.ok) throw new Error('Failed to fetch balance');
        return response.json();
    }

    async getCategories(): Promise<CategoryResponse> {
        const response = await fetch(`${this.baseUrl}/categories`);
        if (!response.ok) throw new Error('Failed to fetch categories');
        return response.json();
    }

    async getMonthlyReport(month: string): Promise<any> {
        const response = await fetch(`${this.baseUrl}/report/${month}`);
        if (!response.ok) throw new Error('Failed to fetch report');
        return response.json();
    }

    async initUser(data: any): Promise<any> {
        const response = await fetch(`${this.baseUrl}/init`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error('Failed to initialize user');
        return response.json();
    }

    // Note: These endpoints would need to be implemented in the backend
    async login(username: string, password: string): Promise<any> {
        // For now, check if user exists in localStorage or use init endpoint
        // In a full implementation, this would call a backend login endpoint
        try {
            const userSetup = localStorage.getItem('userSetup');
            if (userSetup) {
                const userData = JSON.parse(userSetup);
                if (userData.username === username) {
                    return { success: true, user: userData };
                }
            }
            // If no user found, create a simple session
            const user = { username, email: `${username}@example.com`, initialBalance: 0 };
            localStorage.setItem('userSetup', JSON.stringify(user));
            return { success: true, user };
        } catch (error) {
            throw new Error('Login failed');
        }
    }

    async register(data: any): Promise<any> {
        // Use init endpoint to register user
        try {
            const result = await this.initUser(data);
            if (result.success) {
                const user = { 
                    username: data.username, 
                    email: data.email, 
                    initialBalance: data.initialBalance || 0 
                };
                localStorage.setItem('userSetup', JSON.stringify(user));
                return { success: true, user };
            }
            return result;
        } catch (error: any) {
            throw new Error(error.message || 'Registration failed');
        }
    }
}

/**
 * UI Manager Class (OOP Concept: Single Responsibility Principle)
 */
class MoneyMateUI {
    private api: MoneyMateAPI;
    private categories: CategoryResponse | null = null;
    private currentUser: User | null = null;
    private currentPage: string = 'dashboard';

    constructor(api: MoneyMateAPI) {
        this.api = api;
        this.init();
    }

    private async init(): Promise<void> {
        await this.loadCategories();
        this.setupEventListeners();
        this.setupNavigation();
        this.setDefaultDate();
        this.updateAddPreview();
        await this.loadDashboard();
    }

    private async loadCategories(): Promise<void> {
        try {
            this.categories = await this.api.getCategories();
            this.populateCategorySelect();
        } catch (error) {
            console.error('Failed to load categories:', error);
        }
    }

    private populateCategorySelect(): void {
        const categorySelect = document.getElementById('category') as HTMLSelectElement;
        if (!categorySelect || !this.categories) return;

        // Default to income categories
        categorySelect.innerHTML = '';
        this.categories.income.forEach(cat => {
            const option = document.createElement('option');
            // Use enum name (e.g., GAJI) as value, displayName as text
            option.value = cat.name || cat.displayName;
            option.textContent = cat.displayName || cat.name;
            categorySelect.appendChild(option);
        });
    }

    private setupEventListeners(): void {
        // Transaction form
        const addForm = document.getElementById('addTransactionForm') as HTMLFormElement;
        addForm?.addEventListener('submit', (e) => this.handleAddTransaction(e));
        addForm?.addEventListener('reset', () => {
            setTimeout(() => {
                this.setDefaultDate();
                this.updateAddPreview();
            }, 0);
        });

        // Transaction type change
        const typeSelect = document.getElementById('transactionType') as HTMLSelectElement;
        typeSelect?.addEventListener('change', () => this.updateTransactionForm());
        typeSelect?.addEventListener('change', () => this.updateAddPreview());

        // Refresh buttons
        const refreshBtn = document.getElementById('refreshTransactionsBtn') as HTMLButtonElement;
        refreshBtn?.addEventListener('click', () => this.loadTransactionsPage());

        const refreshLogsBtn = document.getElementById('refreshLogsBtn') as HTMLButtonElement;
        refreshLogsBtn?.addEventListener('click', () => this.loadActivityLogs());

        // Report generation
        const reportBtn = document.getElementById('generateReportBtn') as HTMLButtonElement;
        reportBtn?.addEventListener('click', () => this.generateReport());

        // Filter
        const filterType = document.getElementById('filterType') as HTMLSelectElement;
        filterType?.addEventListener('change', () => this.loadTransactionsPage());

        const filterSearch = document.getElementById('filterSearch') as HTMLInputElement;
        filterSearch?.addEventListener('input', () => this.loadTransactionsPage());

        const filterStartDate = document.getElementById('filterStartDate') as HTMLInputElement;
        const filterEndDate = document.getElementById('filterEndDate') as HTMLInputElement;
        filterStartDate?.addEventListener('change', () => this.loadTransactionsPage());
        filterEndDate?.addEventListener('change', () => this.loadTransactionsPage());

        const clearFiltersBtn = document.getElementById('clearFiltersBtn') as HTMLButtonElement;
        clearFiltersBtn?.addEventListener('click', () => {
            if (filterSearch) filterSearch.value = '';
            if (filterStartDate) filterStartDate.value = '';
            if (filterEndDate) filterEndDate.value = '';
            if (filterType) filterType.value = 'all';
            this.loadTransactionsPage();
        });

        // Live preview listeners
        ['amount', 'description', 'date', 'category', 'extra', 'recurring'].forEach(id => {
            const el = document.getElementById(id) as HTMLInputElement | HTMLSelectElement | null;
            el?.addEventListener('input', () => this.updateAddPreview());
            el?.addEventListener('change', () => this.updateAddPreview());
        });

        // Logout
        const logoutBtn = document.getElementById('logoutBtn') as HTMLButtonElement;
        logoutBtn?.addEventListener('click', () => this.logout());
    }

    private setupNavigation(): void {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const page = item.getAttribute('data-page');
                if (page) {
                    this.navigateToPage(page);
                }
            });
        });
    }

    private navigateToPage(page: string): void {
        // Update active nav item
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-page="${page}"]`)?.classList.add('active');

        // Hide all pages
        document.querySelectorAll('.page').forEach(p => {
            (p as HTMLElement).classList.remove('active');
        });

        // Show target page
        const targetPage = document.getElementById(`${page}Page`);
        if (targetPage) {
            targetPage.classList.add('active');
        }

        // Update page title
        const titles: { [key: string]: string } = {
            'dashboard': 'Dashboard',
            'transactions': 'Transactions',
            'add-transaction': 'Add Transaction',
            'reports': 'Reports',
            'activity-logs': 'Activity Logs',
            'profile': 'Profile'
        };
        const titleEl = document.getElementById('pageTitle');
        if (titleEl) {
            titleEl.textContent = titles[page] || 'Dashboard';
        }

        this.currentPage = page;

        // Load page-specific data
        switch (page) {
            case 'dashboard':
                this.loadDashboard();
                break;
            case 'transactions':
                this.loadTransactionsPage();
                break;
            case 'activity-logs':
                this.loadActivityLogs();
                break;
            case 'profile':
                this.loadProfile();
                break;
        }
    }

    private updateTransactionForm(): void {
        const typeSelect = document.getElementById('transactionType') as HTMLSelectElement;
        const categorySelect = document.getElementById('category') as HTMLSelectElement;
        const extraLabel = document.getElementById('extraLabel') as HTMLLabelElement;
        const recurringDiv = document.getElementById('recurringDiv') as HTMLDivElement;
        const extraInput = document.getElementById('extra') as HTMLInputElement;

        if (!typeSelect || !categorySelect || !this.categories) return;

        const isIncome = typeSelect.value === 'income';

        // Update category options
        categorySelect.innerHTML = '';
        const categories = isIncome ? this.categories.income : this.categories.expense;
        categories.forEach(cat => {
            const option = document.createElement('option');
            // Use enum name (e.g., GAJI) as value, displayName as text
            option.value = cat.name || cat.displayName;
            option.textContent = cat.displayName || cat.name;
            categorySelect.appendChild(option);
        });

        // Update label and recurring field
        if (extraLabel) {
            extraLabel.textContent = isIncome ? 'Source *' : 'Payment Method *';
        }
        if (extraInput) {
            extraInput.placeholder = isIncome ? 'Enter source (e.g., Salary, Bonus)' : 'Enter payment method (e.g., Cash, Card)';
        }
        if (recurringDiv) {
            recurringDiv.style.display = isIncome ? 'none' : 'block';
        }

        this.updateAddPreview();
    }

    private async loadDashboard(): Promise<void> {
        try {
            await Promise.all([
                this.updateBalance(),
                this.loadRecentTransactions(),
                this.loadCategoryBreakdown()
            ]);
        } catch (error) {
            console.error('Failed to load dashboard:', error);
            this.showToast('Failed to load dashboard data', 'error');
        }
    }

    private async updateBalance(): Promise<void> {
        try {
            const balance = await this.api.getBalance();

            const balanceEl = document.getElementById('totalBalance');
            const incomeEl = document.getElementById('totalIncome');
            const expenseEl = document.getElementById('totalExpense');

            if (balanceEl) balanceEl.textContent = `Rp ${this.formatNumber(balance.totalBalance)}`;
            if (incomeEl) incomeEl.textContent = `Rp ${this.formatNumber(balance.totalIncome)}`;
            if (expenseEl) expenseEl.textContent = `Rp ${this.formatNumber(balance.totalExpense)}`;

            // Update transaction count
            const transactions = await this.api.getTransactions();
            const countEl = document.getElementById('totalTransactions');
            if (countEl) countEl.textContent = transactions.length.toString();
        } catch (error) {
            console.error('Failed to update balance:', error);
        }
    }

    private async loadRecentTransactions(): Promise<void> {
        try {
            const transactions = await this.api.getTransactions();
            const recent = transactions.slice(0, 5);
            const container = document.getElementById('recentTransactions');

            if (!container) return;

            if (recent.length === 0) {
                container.innerHTML = '<p class="empty-state">No recent transactions</p>';
                return;
            }

            container.innerHTML = recent.map(tx => {
                const escapeHtml = (text: string) => {
                    const div = document.createElement('div');
                    div.textContent = text;
                    return div.innerHTML;
                };
                return `
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid var(--border-color);">
                        <div>
                            <div style="font-weight: 500;">${escapeHtml(tx.description)}</div>
                            <div style="font-size: 12px; color: var(--text-muted);">${this.formatDate(tx.date)}</div>
                        </div>
                        <div style="font-weight: 600; color: ${tx.transactionType === 'PEMASUKAN' ? '#10b981' : '#ef4444'};">
                            ${tx.transactionType === 'PEMASUKAN' ? '+' : '-'}Rp ${this.formatNumber(tx.amount)}
                        </div>
                    </div>
                `;
            }).join('');
        } catch (error) {
            console.error('Failed to load recent transactions:', error);
        }
    }

    private async loadCategoryBreakdown(): Promise<void> {
        try {
            const transactions = await this.api.getTransactions();
            const categoryMap = new Map<string, number>();

            transactions.forEach(tx => {
                // Handle both Category enum name and displayName
                const categoryKey = tx.category || 'Unknown';
                const current = categoryMap.get(categoryKey) || 0;
                categoryMap.set(categoryKey, current + tx.amount);
            });

            const container = document.getElementById('categoryBreakdown');
            if (!container) return;

            if (categoryMap.size === 0) {
                container.innerHTML = '<p class="empty-state">No data available</p>';
                return;
            }

            const sorted = Array.from(categoryMap.entries())
                .sort((a, b) => b[1] - a[1])
                .slice(0, 5);

            container.innerHTML = sorted.map(([category, amount]) => {
                // Try to get display name from categories if available
                let displayName = category;
                if (this.categories) {
                    const allCategories = [...this.categories.income, ...this.categories.expense];
                    const found = allCategories.find(c => c.name === category);
                    if (found) {
                        displayName = found.displayName;
                    }
                }
                return `
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0;">
                        <span>${displayName}</span>
                        <span style="font-weight: 600;">Rp ${this.formatNumber(amount)}</span>
                    </div>
                `;
            }).join('');
        } catch (error) {
            console.error('Failed to load category breakdown:', error);
        }
    }

    private async loadTransactionsPage(): Promise<void> {
        try {
            const transactions = await this.api.getTransactions();
            const filterType = (document.getElementById('filterType') as HTMLSelectElement)?.value;
            const filtered = this.applyTransactionFilters(transactions, filterType);
            this.renderTransactions(filtered);
            this.updateTransactionStats(filtered);
        } catch (error) {
            console.error('Failed to load transactions:', error);
            this.showToast('Failed to load transactions', 'error');
        }
    }

    private applyTransactionFilters(transactions: Transaction[], filterType: string | undefined): Transaction[] {
        const search = (document.getElementById('filterSearch') as HTMLInputElement)?.value?.toLowerCase() || '';
        const startDateStr = (document.getElementById('filterStartDate') as HTMLInputElement)?.value;
        const endDateStr = (document.getElementById('filterEndDate') as HTMLInputElement)?.value;

        return transactions.filter(t => {
            const matchesType =
                filterType === 'income' ? t.transactionType === 'PEMASUKAN' :
                filterType === 'expense' ? t.transactionType === 'PENGELUARAN' : true;

            const haystack = `${t.description} ${t.category}`.toLowerCase();
            const matchesSearch = search ? haystack.includes(search) : true;

            const txDate = new Date(t.date);
            const afterStart = startDateStr ? txDate >= new Date(startDateStr) : true;
            const beforeEnd = endDateStr ? txDate <= new Date(endDateStr) : true;

            return matchesType && matchesSearch && afterStart && beforeEnd;
        });
    }

    private updateTransactionStats(transactions: Transaction[]): void {
        const total = transactions.length;
        const income = transactions
            .filter(t => t.transactionType === 'PEMASUKAN')
            .reduce((sum, t) => sum + t.amount, 0);
        const expense = transactions
            .filter(t => t.transactionType === 'PENGELUARAN')
            .reduce((sum, t) => sum + t.amount, 0);
        const balance = income - expense;

        const setText = (id: string, value: string) => {
            const el = document.getElementById(id);
            if (el) el.textContent = value;
        };

        setText('txStatTotal', total.toString());
        setText('txStatIncome', `Rp ${this.formatNumber(income)}`);
        setText('txStatExpense', `Rp ${this.formatNumber(expense)}`);
        setText('txStatBalance', `Rp ${this.formatNumber(balance)}`);
    }

    private renderTransactions(transactions: Transaction[]): void {
        const tbody = document.getElementById('transactionTableBody');
        if (!tbody) return;

        if (transactions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="loading-state">No transactions found</td></tr>';
            return;
        }

        tbody.innerHTML = transactions.map(tx => {
            const details = tx.transactionType === 'PEMASUKAN' 
                ? `Source: ${tx.source || 'N/A'}`
                : `Payment: ${tx.paymentMethod || 'N/A'}${tx.isRecurring ? ' [Recurring]' : ''}`;

            // Get category display name
            let categoryDisplay = tx.category;
            if (this.categories) {
                const allCategories = [...this.categories.income, ...this.categories.expense];
                const found = allCategories.find(c => c.name === tx.category);
                if (found) {
                    categoryDisplay = found.displayName;
                }
            }

            // Escape HTML to prevent XSS
            const escapeHtml = (text: string) => {
                const div = document.createElement('div');
                div.textContent = text;
                return div.innerHTML;
            };

            return `
                <tr>
                    <td>${escapeHtml(tx.transactionId)}</td>
                    <td><span class="badge ${tx.transactionType === 'PEMASUKAN' ? 'badge-income' : 'badge-expense'}">${tx.transactionType === 'PEMASUKAN' ? 'Income' : 'Expense'}</span></td>
                    <td>${this.formatDate(tx.date)}</td>
                    <td>${escapeHtml(categoryDisplay)}</td>
                    <td>${escapeHtml(tx.description)}</td>
                    <td style="font-weight: 600; color: ${tx.transactionType === 'PEMASUKAN' ? '#10b981' : '#ef4444'};">Rp ${this.formatNumber(tx.amount)}</td>
                    <td style="font-size: 12px; color: var(--text-muted);">${escapeHtml(details)}</td>
                    <td>
                        <button class="btn btn-danger btn-sm" onclick="app.deleteTransaction('${escapeHtml(tx.transactionId)}')">
                            🗑️ Delete
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }

    private async handleAddTransaction(e: Event): Promise<void> {
        e.preventDefault();

        const form = e.target as HTMLFormElement;
        const formData = new FormData(form);

        const data = {
            type: formData.get('type'),
            amount: parseFloat(formData.get('amount') as string),
            description: formData.get('description'),
            date: formData.get('date'),
            category: formData.get('category'),
            source: formData.get('type') === 'income' ? formData.get('extra') : null,
            paymentMethod: formData.get('type') === 'expense' ? formData.get('extra') : null,
            recurring: formData.get('recurring') === 'on'
        };

        try {
            const result = await this.api.addTransaction(data);
            if (result.success) {
                this.showToast('Transaction added successfully!', 'success');
                form.reset();
                // Set default date to today
                const dateInput = document.getElementById('date') as HTMLInputElement;
                if (dateInput) {
                    dateInput.value = new Date().toISOString().split('T')[0];
                }
                this.updateAddPreview();
                await this.loadDashboard();
                this.navigateToPage('transactions');
            } else {
                this.showToast(result.error || 'Failed to add transaction', 'error');
            }
        } catch (error: any) {
            this.showToast(error.message || 'Failed to add transaction', 'error');
        }
    }

    async deleteTransaction(id: string): Promise<void> {
        if (!confirm('Are you sure you want to delete this transaction?')) return;

        try {
            await this.api.deleteTransaction(id);
            this.showToast('Transaction deleted successfully!', 'success');
            await this.loadTransactionsPage();
            await this.loadDashboard();
        } catch (error) {
            this.showToast('Failed to delete transaction', 'error');
        }
    }

    private async generateReport(): Promise<void> {
        const monthInput = document.getElementById('reportMonth') as HTMLInputElement;
        if (!monthInput) return;

        let month = monthInput.value;
        if (!month) {
            const now = new Date();
            month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
            monthInput.value = month;
        }

        try {
            const report = await this.api.getMonthlyReport(month);
            const container = document.getElementById('reportContent');
            if (!container) return;

            container.innerHTML = `
                <div style="padding: 24px;">
                    <h3 style="margin-bottom: 24px; color: var(--text-primary);">Monthly Report - ${month}</h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
                        <div style="background: var(--bg-secondary); padding: 16px; border-radius: var(--radius-md);">
                            <div style="color: var(--text-muted); font-size: 12px; margin-bottom: 8px;">Total Income</div>
                            <div style="font-size: 24px; font-weight: 700; color: #10b981;">Rp ${this.formatNumber(report.totalIncome)}</div>
                        </div>
                        <div style="background: var(--bg-secondary); padding: 16px; border-radius: var(--radius-md);">
                            <div style="color: var(--text-muted); font-size: 12px; margin-bottom: 8px;">Total Expense</div>
                            <div style="font-size: 24px; font-weight: 700; color: #ef4444;">Rp ${this.formatNumber(report.totalExpense)}</div>
                        </div>
                        <div style="background: var(--bg-secondary); padding: 16px; border-radius: var(--radius-md);">
                            <div style="color: var(--text-muted); font-size: 12px; margin-bottom: 8px;">Balance</div>
                            <div style="font-size: 24px; font-weight: 700; color: var(--primary-color);">Rp ${this.formatNumber(report.balance)}</div>
                        </div>
                    </div>
                    <pre style="background: var(--bg-secondary); padding: 16px; border-radius: var(--radius-md); overflow-x: auto; font-family: 'Courier New', monospace; font-size: 12px; color: var(--text-secondary);">${report.summary || 'No detailed summary available'}</pre>
                </div>
            `;
        } catch (error) {
            this.showToast('Failed to generate report', 'error');
        }
    }

    private async loadActivityLogs(): Promise<void> {
        const container = document.getElementById('activityLogsTableBody');
        if (!container) return;

        try {
            // Try to fetch activity logs if endpoint exists
            // For now, show a message that this feature is available in the desktop app
            container.innerHTML = `
                <tr>
                    <td colspan="3" class="loading-state">
                        <div style="text-align: center; padding: 40px;">
                            <div style="font-size: 48px; margin-bottom: 16px;">📋</div>
                            <h3 style="margin-bottom: 8px; color: var(--text-primary);">Activity Logs</h3>
                            <p style="color: var(--text-muted); margin-bottom: 16px;">
                                Activity logging is available in the desktop application.
                            </p>
                            <p style="color: var(--text-muted); font-size: 12px;">
                                All your transactions and actions are automatically logged to the database.
                            </p>
                        </div>
                    </td>
                </tr>
            `;
        } catch (error) {
            console.error('Failed to load activity logs:', error);
            container.innerHTML = '<tr><td colspan="3" class="loading-state">Failed to load activity logs</td></tr>';
        }
    }

    private async loadProfile(): Promise<void> {
        if (!this.currentUser) return;

        const usernameEl = document.getElementById('profileUsername');
        const emailEl = document.getElementById('profileEmail');
        const userIdEl = document.getElementById('profileUserId');
        const balanceEl = document.getElementById('profileInitialBalance');
        const createdAtEl = document.getElementById('profileCreatedAt');

        if (usernameEl) usernameEl.textContent = this.currentUser.username;
        if (emailEl) emailEl.textContent = this.currentUser.email;
        if (userIdEl) userIdEl.textContent = this.currentUser.userId || 'N/A';
        if (balanceEl) balanceEl.textContent = `Rp ${this.formatNumber(this.currentUser.initialBalance || 0)}`;
        if (createdAtEl) createdAtEl.textContent = this.currentUser.createdAt || 'N/A';
    }

    private logout(): void {
        if (confirm('Are you sure you want to logout?')) {
            localStorage.removeItem('userSetup');
            localStorage.removeItem('currentUser');
            window.location.reload();
        }
    }

    private formatNumber(num: number): string {
        return num.toLocaleString('id-ID', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    private formatDate(dateStr: string): string {
        const date = new Date(dateStr);
        return date.toLocaleDateString('id-ID', { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric' 
        });
    }

    private setDefaultDate(): void {
        const dateInput = document.getElementById('date') as HTMLInputElement;
        if (dateInput) {
            dateInput.value = new Date().toISOString().split('T')[0];
        }
    }

    private updateAddPreview(): void {
        const typeSelect = document.getElementById('transactionType') as HTMLSelectElement;
        const amountInput = document.getElementById('amount') as HTMLInputElement;
        const descInput = document.getElementById('description') as HTMLInputElement;
        const dateInput = document.getElementById('date') as HTMLInputElement;
        const categorySelect = document.getElementById('category') as HTMLSelectElement;
        const extraInput = document.getElementById('extra') as HTMLInputElement;
        const recurringInput = document.getElementById('recurring') as HTMLInputElement;

        const type = typeSelect?.value === 'expense' ? 'Expense' : 'Income';
        const amount = parseFloat(amountInput?.value || '0') || 0;
        const sign = type === 'Income' ? '+' : '-';
        const date = dateInput?.value || '-';
        const category = categorySelect?.selectedOptions?.[0]?.text || '-';
        const description = descInput?.value || '-';
        const detail = extraInput?.value || '-';
        const recurring = recurringInput?.checked ? 'Yes' : 'No';

        const typeBadge = document.getElementById('previewType');
        const amountEl = document.getElementById('previewAmount');
        const dateEl = document.getElementById('previewDate');
        const catEl = document.getElementById('previewCategory');
        const descEl = document.getElementById('previewDescription');
        const detailEl = document.getElementById('previewDetail');
        const recurEl = document.getElementById('previewRecurring');

        if (typeBadge) {
            typeBadge.textContent = type;
            typeBadge.className = `preview-badge ${type === 'Income' ? 'badge-income' : 'badge-expense'}`;
        }
        if (amountEl) amountEl.textContent = `${sign} Rp ${this.formatNumber(amount)}`;
        if (dateEl) dateEl.textContent = date;
        if (catEl) catEl.textContent = category;
        if (descEl) descEl.textContent = description;
        if (detailEl) detailEl.textContent = detail;
        if (recurEl) recurEl.textContent = recurring;
    }

    private showToast(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
        showToast(message, type);
    }

    setCurrentUser(user: User): void {
        this.currentUser = user;
        const userDisplayName = document.getElementById('userDisplayName');
        if (userDisplayName) {
            userDisplayName.textContent = user.username;
        }
    }
}

// Global toast function
function showToast(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    const toast = document.getElementById('toast');
    if (!toast) {
        // Create toast if it doesn't exist
        const toastEl = document.createElement('div');
        toastEl.id = 'toast';
        toastEl.className = 'toast';
        document.body.appendChild(toastEl);
        return showToast(message, type);
    }

    toast.textContent = message;
    toast.className = `toast ${type} show`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Initialize app
let app: MoneyMateUI | undefined;
const api = new MoneyMateAPI(API_BASE_URL);

// Auth handlers
document.addEventListener('DOMContentLoaded', () => {
    // Tab switching
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const tab = btn.getAttribute('data-tab');
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
            btn.classList.add('active');
            const form = document.getElementById(`${tab}Form`);
            if (form) form.classList.add('active');
        });
    });

    // Login form
    const loginForm = document.getElementById('loginFormElement') as HTMLFormElement;
    loginForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const usernameInput = document.getElementById('loginUsername') as HTMLInputElement;
        const passwordInput = document.getElementById('loginPassword') as HTMLInputElement;
        const submitBtn = loginForm.querySelector('button[type="submit"]') as HTMLButtonElement;
        
        const username = usernameInput.value.trim();
        const password = passwordInput.value;

        if (!username || !password) {
            showToast('Please fill in all fields', 'error');
            return;
        }

        // Show loading state
        if (submitBtn) {
            const loader = submitBtn.querySelector('.btn-loader') as HTMLElement;
            const btnText = submitBtn.querySelector('span:not(.btn-loader)') as HTMLElement;
            if (loader) loader.style.display = 'inline';
            if (btnText) btnText.style.display = 'none';
            submitBtn.disabled = true;
        }

        try {
            const result = await api.login(username, password);
            if (result.success) {
                showToast('Login successful!', 'success');
                showMainApp(result.user);
            } else {
                showToast('Login failed. Please check your credentials.', 'error');
            }
        } catch (error: any) {
            showToast(error.message || 'Login failed. Please try again.', 'error');
        } finally {
            // Reset button state
            if (submitBtn) {
                const loader = submitBtn.querySelector('.btn-loader') as HTMLElement;
                const btnText = submitBtn.querySelector('span:not(.btn-loader)') as HTMLElement;
                if (loader) loader.style.display = 'none';
                if (btnText) btnText.style.display = 'inline';
                submitBtn.disabled = false;
            }
        }
    });

    // Register form
    const registerForm = document.getElementById('registerFormElement') as HTMLFormElement;
    registerForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const usernameInput = document.getElementById('registerUsername') as HTMLInputElement;
        const emailInput = document.getElementById('registerEmail') as HTMLInputElement;
        const passwordInput = document.getElementById('registerPassword') as HTMLInputElement;
        const balanceInput = document.getElementById('registerBalance') as HTMLInputElement;
        const submitBtn = registerForm.querySelector('button[type="submit"]') as HTMLButtonElement;
        
        const username = usernameInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value;
        const initialBalance = parseFloat(balanceInput.value) || 0;

        // Validation
        if (!username || !email || !password) {
            showToast('Please fill in all required fields', 'error');
            return;
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showToast('Please enter a valid email address', 'error');
            return;
        }

        // Show loading state
        if (submitBtn) {
            const loader = submitBtn.querySelector('.btn-loader') as HTMLElement;
            const btnText = submitBtn.querySelector('span:not(.btn-loader)') as HTMLElement;
            if (loader) loader.style.display = 'inline';
            if (btnText) btnText.style.display = 'none';
            submitBtn.disabled = true;
        }

        try {
            const result = await api.register({ username, email, password, initialBalance });
            if (result.success) {
                const user = { username, email, initialBalance };
                localStorage.setItem('userSetup', JSON.stringify(user));
                showToast('Registration successful!', 'success');
                showMainApp(user);
            } else {
                showToast(result.error || 'Registration failed', 'error');
            }
        } catch (error: any) {
            showToast(error.message || 'Registration failed. Please try again.', 'error');
        } finally {
            // Reset button state
            if (submitBtn) {
                const loader = submitBtn.querySelector('.btn-loader') as HTMLElement;
                const btnText = submitBtn.querySelector('span:not(.btn-loader)') as HTMLElement;
                if (loader) loader.style.display = 'none';
                if (btnText) btnText.style.display = 'inline';
                submitBtn.disabled = false;
            }
        }
    });

    // Check if user is already logged in
    const userSetup = localStorage.getItem('userSetup');
    if (userSetup) {
        const userData = JSON.parse(userSetup);
        showMainApp(userData);
    } else {
        showAuthModal();
    }
});

function showAuthModal(): void {
    const modal = document.getElementById('authModal');
    const mainApp = document.getElementById('mainApp');
    if (modal) modal.style.display = 'flex';
    if (mainApp) mainApp.style.display = 'none';
}

function showMainApp(userData: any): void {
    const modal = document.getElementById('authModal');
    const mainApp = document.getElementById('mainApp');
    const userWelcome = document.getElementById('userWelcome');

    if (modal) modal.style.display = 'none';
    if (mainApp) mainApp.style.display = 'flex';
    if (userWelcome) {
        userWelcome.textContent = `Welcome back, ${userData.username}!`;
    }

    if (!app) {
        app = new MoneyMateUI(api);
        app.setCurrentUser(userData);
        (window as any).app = app;

        // Set default date to today
        const dateInput = document.getElementById('date') as HTMLInputElement;
        if (dateInput) {
            dateInput.value = new Date().toISOString().split('T')[0];
        }
    }
}
