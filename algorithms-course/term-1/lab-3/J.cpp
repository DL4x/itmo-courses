#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

ll gcd(ll a, ll b) {
    while (b > 0) {
        a %= b;
        swap(a, b);
    }
    return a;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, d;
    cin >> n >> d;

    vector<ll> nums;
    for (int i = 0; i < n; i++) {
        ll x;
        cin >> x;
        if (!(x % d)) {
            nums.push_back(x);
        }
    }

    if (nums.empty()) {
        cout << -1 << endl;
        return 0;
    }

    ll g = nums[0];
    for (int i = 1; i < nums.size(); i++) {
        g = gcd(g, nums[i]);
    }

    if (g != d) {
        cout << -1 << endl;
        return 0;
    }

    cout << nums.size() << '\n';
    for (auto x : nums) {
        cout << x << " ";
    }
    cout << endl;

    return 0;
}
