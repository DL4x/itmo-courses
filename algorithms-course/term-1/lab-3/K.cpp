#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

struct r_gcd {
    ll g, x, y;
};

r_gcd gcd(ll a, ll b) {
    if (a == 0) {
        return {b, 0, 1};
    }
    auto [g, x, y] = gcd(b % a, a);
    return {g, y - (b / a) * x, x};
}

ll inverse(ll a, ll mod) {
    auto [g, x, _] = gcd(a, mod);
    if (g != 1) {
        return -1;
    }
    return x % mod;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        ll a, b, n, m;
        cin >> a >> b >> n >> m;

        ll n_inv = inverse(n, m);

        ll k = (((b - a) * n_inv % m) + m) % m;

        cout << (a + k * n) % (n * m) << endl;
    }

    return 0;
}
