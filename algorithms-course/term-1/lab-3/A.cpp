#pragma GCC optimize("O2")
 
#include <iostream>
 
using namespace std;
 
typedef long long ll;
 
bool prime(ll n) {
    for (ll i = 2; i * i <= n; i++) {
        if (n % i == 0) {
            return false;
        }
    }
 
    return true;
}
 
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    ll n;
    cin >> n;
 
    cout << (prime(n) ? "True" : "False") << endl;
 
    return 0;
}
