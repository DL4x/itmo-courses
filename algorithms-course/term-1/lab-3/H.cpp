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

    int a, b, c, d;
    cin >> a >> b >> c >> d;

    int g1 = gcd(b, d);
    int lcm = b * d / g1;

    int sum = a * lcm / b + c * lcm / d;

    int g2 = gcd(abs(sum), lcm);

    cout << sum / g2 << " " << lcm / g2 << endl;

    return 0;
}
