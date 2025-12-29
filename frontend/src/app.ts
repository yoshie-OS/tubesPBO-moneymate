/**
 * MoneyMate TypeScript Frontend
 *
 * OOP Concepts in TypeScript:
 * - Classes and Interfaces
 * - Encapsulation
 * - Type Safety
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
        return response.json();
    }

    async addTransaction(data: any): Promise<any> {
        const response = await fetch(`${this.baseUrl}/transactions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return response.json();
    }

    async deleteTransaction(id: string): Promise<any> {
        const response = await fetch(`${this.baseUrl}/transactions/${id}`, {
            method: 'DELETE'
        });
        return response.json();
    }

    async getBalance(): Promise<Balance> {
        const response = await fetch(`${this.baseUrl}/balance`);
        return response.json();
    }

    async getCategories(): Promise<any> {
        const response = await fetch(`${this.baseUrl}/categories`);
        return response.json();
    }

    async getMonthlyReport(month: string): Promise<any> {
        const response = await fetch(`${this.baseUrl}/report/${month}`);
        return response.json();
    }

    async initUser(data: any): Promise<any> {
        const response = await fetch(`${this.baseUrl}/init`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return response.json();
    }
}

/**
 * UI Manager Class (OOP Concept: Single Responsibility Principle)
 */
class MoneyMateUI {
    private api: MoneyMateAPI;
    private categories: any = null;

    constructor(api: MoneyMateAPI) {
        this.api = api;
        this.init();
    }

    private async init(): Promise<void> {
        this.setupEventListeners();
        await this.loadTransactions();
        await this.updateBalance();
    }

    private setupEventListeners(): void {
        const addForm = document.getElementById('addTransactionForm') as HTMLFormElement;
        const typeSelect = document.getElementById('transactionType') as HTMLSelectElement;
        const refreshBtn = document.getElementById('refreshBtn') as HTMLButtonElement;
        const reportBtn = document.getElementById('reportBtn') as HTMLButtonElement;

        addForm?.addEventListener('submit', (e) => this.handleAddTransaction(e));
        typeSelect?.addEventListener('change', () => this.updateExtraLabel());
        refreshBtn?.addEventListener('click', () => this.refresh());
        reportBtn?.addEventListener('click', () => this.showMonthlyReport());
    }

    private updateExtraLabel(): void {
        const typeSelect = document.getElementById('transactionType') as HTMLSelectElement;
        const extraLabel = document.getElementById('extraLabel') as HTMLLabelElement;
        const recurringDiv = document.getElementById('recurringDiv') as HTMLDivElement;

        if (!typeSelect) return;

        const isIncome = typeSelect.value === 'income';

        if (extraLabel) {
            extraLabel.textContent = isIncome ? 'Sumber:' : 'Metode Pembayaran:';
        }

        if (recurringDiv) {
            recurringDiv.style.display = isIncome ? 'none' : 'block';
        }
    }

    private async loadTransactions(): Promise<void> {
        try {
            const transactions = await this.api.getTransactions();
            this.renderTransactions(transactions);
        } catch (error) {
            console.error('Failed to load transactions:', error);
        }
    }

    private renderTransactions(transactions: Transaction[]): void {
        const tbody = document.getElementById('transactionTableBody');
        if (!tbody) return;

        tbody.innerHTML = '';

        transactions.forEach(tx => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${tx.transactionId}</td>
                <td><span class="badge ${tx.transactionType === 'PEMASUKAN' ? 'badge-income' : 'badge-expense'}">${tx.transactionType}</span></td>
                <td>${this.formatDate(tx.date)}</td>
                <td>${tx.category}</td>
                <td>${tx.description}</td>
                <td class="${tx.transactionType === 'PEMASUKAN' ? 'text-success' : 'text-danger'}">Rp ${this.formatNumber(tx.amount)}</td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="app.deleteTransaction('${tx.transactionId}')">
                        🗑️ Hapus
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
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
        } catch (error) {
            console.error('Failed to update balance:', error);
        }
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
            source: formData.get('extra'),
            paymentMethod: formData.get('extra'),
            recurring: formData.get('recurring') === 'on'
        };

        try {
            const result = await this.api.addTransaction(data);

            if (result.success) {
                alert('✅ Transaksi berhasil ditambahkan!');
                form.reset();
                await this.refresh();
            } else {
                alert('❌ ' + (result.error || 'Gagal menambahkan transaksi'));
            }
        } catch (error) {
            alert('❌ Error: ' + error);
        }
    }

    async deleteTransaction(id: string): Promise<void> {
        if (!confirm('Hapus transaksi ini?')) return;

        try {
            await this.api.deleteTransaction(id);
            alert('✅ Transaksi berhasil dihapus!');
            await this.refresh();
        } catch (error) {
            alert('❌ Gagal menghapus transaksi');
        }
    }

    private async refresh(): Promise<void> {
        await this.loadTransactions();
        await this.updateBalance();
    }

    private async showMonthlyReport(): Promise<void> {
        const month = prompt('Masukkan bulan (YYYY-MM):', this.getCurrentMonth());
        if (!month) return;

        try {
            const report = await this.api.getMonthlyReport(month);

            const summary = `
=== LAPORAN BULANAN ${month} ===

Total Pemasukan: Rp ${this.formatNumber(report.totalIncome)}
Total Pengeluaran: Rp ${this.formatNumber(report.totalExpense)}
Saldo: Rp ${this.formatNumber(report.balance)}

${report.summary}
            `;

            alert(summary);
        } catch (error) {
            alert('❌ Gagal membuat laporan. Format: YYYY-MM');
        }
    }

    private formatNumber(num: number): string {
        return num.toLocaleString('id-ID', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    private formatDate(dateStr: string): string {
        const date = new Date(dateStr);
        return date.toLocaleDateString('id-ID');
    }

    private getCurrentMonth(): string {
        const now = new Date();
        return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    }
}

// Initialize app when DOM is ready
let app: MoneyMateUI | undefined;
const api = new MoneyMateAPI(API_BASE_URL);
let setupHandled = false;

document.addEventListener('DOMContentLoaded', () => {
    const userSetup = localStorage.getItem('userSetup');

    if (userSetup) {
        const userData = JSON.parse(userSetup);
        showMainApp(userData);
    } else {
        showUserSetupModal();
    }
});

function showUserSetupModal(): void {
    const modal = document.getElementById('userSetupModal');
    const form = document.getElementById('userSetupForm') as HTMLFormElement;
    const mainApp = document.getElementById('mainApp');

    if (modal) modal.style.display = 'flex';
    if (mainApp) mainApp.style.display = 'none';

    if (!setupHandled && form) {
        setupHandled = true;
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = (document.getElementById('setupUsername') as HTMLInputElement).value;
            const email = (document.getElementById('setupEmail') as HTMLInputElement).value;
            const initialBalance = parseFloat((document.getElementById('setupInitialBalance') as HTMLInputElement).value) || 0;

            if (!username || !email) {
                alert('Username dan email harus diisi!');
                return;
            }

            const userData = { username, email, initialBalance };

            try {
                await api.initUser(userData);
                localStorage.setItem('userSetup', JSON.stringify(userData));

                if (modal) modal.style.display = 'none';
                showMainApp(userData);
            } catch (error) {
                console.error('Error:', error);
                alert('Gagal setup user. Cek console.');
            }
        });
    }
}

function showMainApp(userData: any): void {
    const mainApp = document.getElementById('mainApp');
    const userWelcome = document.getElementById('userWelcome');
    const modal = document.getElementById('userSetupModal');

    if (modal) modal.style.display = 'none';
    if (mainApp) mainApp.style.display = 'block';
    if (userWelcome) {
        userWelcome.textContent = `Selamat datang, ${userData.username}! (${userData.email})`;
    }

    if (!app) {
        app = new MoneyMateUI(api);
        (window as any).app = app;
    }
}
