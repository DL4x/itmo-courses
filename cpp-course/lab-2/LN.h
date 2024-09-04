#include "number.h"

#include <iostream>

class LN
{
  public:
	explicit LN(long long num = 0);

	LN(const LN &);
	LN(LN &&) noexcept;

	explicit LN(const char *);
	explicit LN(std::string_view);

	~LN();

	LN &operator=(const LN &);
	LN &operator=(LN &&) noexcept;
	friend LN operator-(const LN &);
	friend LN operator~(const LN &);
	friend LN operator+(const LN &, const LN &);
	friend LN operator-(const LN &, const LN &);
	friend LN operator*(const LN &, const LN &);
	friend LN operator/(const LN &, const LN &);
	friend LN operator%(const LN &, const LN &);
	LN &operator+=(const LN &);
	LN &operator-=(const LN &);
	LN &operator*=(const LN &);
	LN &operator/=(const LN &);
	LN &operator%=(const LN &);
	friend bool operator<(const LN &, const LN &);
	friend bool operator<=(const LN &, const LN &);
	friend bool operator>(const LN &, const LN &);
	friend bool operator>=(const LN &, const LN &);
	friend bool operator==(const LN &, const LN &);
	friend bool operator!=(const LN &, const LN &);
	explicit operator long long();
	explicit operator bool() const;

	void to_string(std::ostream &);

  private:
	bool NaN_ = false;
	bool sign_ = false;
	Number digits_;

	static uint8_t char_to_uint(char);
	static char uint_to_char(uint8_t);
	static void remove_leading_zeros(LN &);
	static LN add(const LN &, const LN &, bool);
	static LN subtract(const LN &, const LN &, bool);
	static int comparator(const LN &, const LN &, bool);
};

LN operator"" _ln(const char *);
