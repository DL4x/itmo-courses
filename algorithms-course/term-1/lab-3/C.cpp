#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

vector<bool> prime;

void init() {
    const int n = 2 * 1e7;
    prime.resize(n + 1, true);
    prime[0] = prime[1] = false;

    for (ll i = 2; i <= n; i++) {
        if (prime[i] && i * i <= n) {
            for (ll j = i * i; j <= n; j += i) {
                prime[j] = false;
            }
        }
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
        cout << (prime[x] ? "YES" : "NO") << '\n';
    }

    return 0;
}
