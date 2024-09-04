#pragma GCC optimize("O2")

#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

class fenwick {
public:
    explicit fenwick(const int n) {
        this->n = n;
        this->v.resize(n + 1, INT_MAX);
        this->BIT1.resize(n + 1, INT_MAX);
        this->BIT2.resize(n + 1, INT_MAX);
    }

    void update(int i, int val) {
        int t = v[i] = val;
        for (int j = i, l = i - 1, r = i + 1; j <= n; j += f(j)) {
            while (l > j - f(j)) {
                t = min(BIT1[l], t);
                l -= f(l);
            }
            while (r < j) {
                t = min(t, BIT2[r]);
                r += f(r);
            }
            BIT1[j] = i ^ j ? min(t, v[j]) : t;
        }
        t = val;
        for (int j = i, l = i - 1, r = i + 1; j; j -= f(j)) {
            while (l > j) {
                t = min(BIT1[l], t);
                l -= f(l);
            }
            while (r <= n && r < j + f(j)) {
                t = min(t, BIT2[r]);
                r += f(r);
            }
            BIT2[j] = i ^ j ? min(v[j], t) : t;
        }
    }

    int query(int l, int r) {
        int i;
        int L = INT_MAX;
        int R = INT_MAX;
        for (i = l; i + f(i) <= r; i += f(i)) {
            L = min(L, BIT2[i]);
        }
        for (i = r; i - f(i) >= l; i -= f(i)) {
            R = min(BIT1[i], R);
        }
        return min(min(L, v[i]), R);
    }

private:
    int n;
    vector<int> v, BIT1, BIT2;

    static int f(int i) {
        return i & -i;
    }
};

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;

    fenwick tree(n);
    for (int i = 1; i <= n; i++) {
        int x;
        cin >> x;
        tree.update(i, x);
    }

    for (int i = 0; i < m; i++) {
        int op;
        cin >> op;
        if (op == 1) {
            int ind, val;
            cin >> ind >> val;
            tree.update(++ind, val);
        } else {
            int l, r;
            cin >> l >> r;
            cout << tree.query(++l, r) << endl;
        }
    }

    return 0;
}
