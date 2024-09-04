#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

int gcd(int a, int b) {
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

    int n;
    cin >> n;

    int g;
    cin >> g;
    for (int i = 0; i < n - 1; i++) {
        int x;
        cin >> x;

        g = gcd(g, x);
    }

    cout << g << endl;

    return 0;
}
