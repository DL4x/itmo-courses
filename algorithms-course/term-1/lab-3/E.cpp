#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

vector<int> prime;

void init() {
    const int n = 2 * 1e6;
    prime.resize(n + 1);

    for (ll i = 2; i * i <= n; i++) {
        if (!prime[i]) {
            for (ll j = i * i; j <= n; j += i) {
                if (!prime[j]) {
                    prime[j] = (int) i;
                }
            }
        }
    }
}

void factorization(ll n) {
    while (true) {
        if (!prime[n]) {
            cout << n << '\n';
            break;
        }
        cout << prime[n] << " ";
        n /= prime[n];
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    init();

    ll n;
    cin >> n;

    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;

        factorization(x);
    }

    return 0;
}
