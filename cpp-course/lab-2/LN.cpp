#include "LN.h"

#include <climits>
#include <cmath>
#include <cstring>

// Constructor from ll
LN::LN(long long num)
{
	sign_ = num < 0;
	num = std::abs(num);
	do
	{
		digits_.push_back(num % 16);
		num /= 16;
	} while (num != 0);
}

// Copy constructor
LN::LN(const LN &num)
{
	this->NaN_ = num.NaN_;
	this->sign_ = num.sign_;
	this->digits_ = num.digits_;
}

// Move constructor
LN::LN(LN &&num) noexcept
{
	this->NaN_ = num.NaN_;
	this->sign_ = num.sign_;
	this->digits_ = num.digits_;
	num.NaN_ = false;
	num.sign_ = false;
	num.digits_.clear();
}

// Constructor from char *
LN::LN(const char *num)
{
	sign_ = num[0] == '-';
	for (size_t i = strlen(num); i-- > (sign_ ? 1 : 0);)
	{
		digits_.push_back(char_to_uint(num[i]));
	}
}

// Constructor from string_view
LN::LN(std::string_view num)
{
	sign_ = num[0] == '-';
	for (size_t i = num.length(); i-- > (sign_ ? 1 : 0);)
	{
		digits_.push_back(char_to_uint(num[i]));
	}
}

// Destructor
LN::~LN()
{
	this->digits_.clear();
}

// Copy assignment operator
LN &LN::operator=(const LN &num)
{
	if (this != &num)
	{
		this->NaN_ = num.NaN_;
		this->sign_ = num.sign_;
		this->digits_ = num.digits_;
	}
	return *this;
}

// Moving assignment operator
LN &LN::operator=(LN &&num) noexcept
{
	if (this != &num)
	{
		this->NaN_ = num.NaN_;
		this->sign_ = num.sign_;
		this->digits_ = num.digits_;
		num.NaN_ = false;
		num.sign_ = false;
		num.digits_.clear();
	}
	return *this;
}

// Unary minus operator
LN operator-(const LN &num)
{
	LN result = num;
	if (num.NaN_)
	{
		return result;
	}
	result.sign_ = !num.sign_;
	return result;
}

// Square root operator
LN operator~(const LN &num)
{
	LN result;
	result.digits_.pop_back();
	if (num.NaN_ || num.sign_)
	{
		result.NaN_ = true;
		return result;
	}
	result.digits_.fill(num.digits_.length(), 0);
	for (size_t i = result.digits_.length(); i-- > 0;)
	{
		for (size_t j = 0; j < 16; j++)
		{
			int compare_result = LN::comparator(result * result, num, false);
			if (compare_result == 1)
			{
				break;
			}
			result.digits_[i]++;
		}
		result.digits_[i]--;
	}
	LN::remove_leading_zeros(result);
	return result;
}

// Addition operator
LN operator+(const LN &num1, const LN &num2)
{
	if (!num1.sign_ && !num2.sign_)
	{
		return LN::add(num1, num2, false);
	}
	else if (num1.sign_ && !num2.sign_)
	{
		int compare_result = LN::comparator(num1, num2, false);
		if (compare_result == -1 || compare_result == 0)
		{
			return LN::subtract(num2, num1, false);
		}
		else
		{
			return LN::subtract(num1, num2, true);
		}
	}
	else if (!num1.sign_ && num2.sign_)
	{
		int compare_result = LN::comparator(num1, num2, false);
		if (compare_result == -1 || compare_result == 1)
		{
			return LN::subtract(num1, num2, false);
		}
		else
		{
			return LN::subtract(num2, num1, true);
		}
	}
	else
	{
		return LN::add(num1, num2, true);
	}
}

// Subtraction operator
LN operator-(const LN &num1, const LN &num2)
{
	if (!num1.sign_ && !num2.sign_)
	{
		int compare_result = LN::comparator(num1, num2, false);
		if (compare_result == -1 || compare_result == 1)
		{
			return LN::subtract(num1, num2, false);
		}
		else
		{
			return LN::subtract(num2, num1, true);
		}
	}
	else if (num1.sign_ && !num2.sign_)
	{
		return LN::add(num1, num2, true);
	}
	else if (!num1.sign_ && num2.sign_)
	{
		return LN::add(num1, num2, false);
	}
	else
	{
		int compare_result = LN::comparator(num1, num2, false);
		if (compare_result == -1 || compare_result == 0)
		{
			return LN::subtract(num2, num1, false);
		}
		else
		{
			return LN::subtract(num1, num2, true);
		}
	}
}

// Multiplication operator
LN operator*(const LN &num1, const LN &num2)
{
	LN result;
	result.digits_.pop_back();
	if (num1.NaN_ || num2.NaN_)
	{
		result.NaN_ = true;
		return result;
	}
	result.sign_ = num1.sign_ != num2.sign_;
	result.digits_.fill(num1.digits_.length() * num2.digits_.length() + 1, 0);
	for (size_t i = 0; i < num1.digits_.length(); i++)
	{
		uint8_t transitional_digit = 0;
		for (size_t j = 0; j < num2.digits_.length(); j++)
		{
			uint8_t current_digit = num1.digits_[i] * num2.digits_[j] + result.digits_[i + j] + transitional_digit;
			transitional_digit = current_digit / 16;
			result.digits_[i + j] = current_digit % 16;
		}
		size_t current_digit = num2.digits_.length();
		while (transitional_digit)
		{
			result.digits_[i + current_digit] = transitional_digit % 16;
			current_digit++;
			transitional_digit /= 16;
		}
	}
	LN::remove_leading_zeros(result);
	return result;
}

// Division operator
LN operator/(const LN &num1, const LN &num2)
{
	LN result;
	result.digits_.pop_back();
	if (num1.NaN_ || num2.NaN_)
	{
		result.NaN_ = true;
		return result;
	}
	if (LN::comparator(num2, 0_ln, false) == -1)
	{
		result.NaN_ = true;
		return result;
	}
	result.sign_ = num1.sign_ != num2.sign_;
	size_t result_length = std::max(num1.digits_.length(), num2.digits_.length());
	result.digits_.fill(result_length, 0);
	for (size_t i = result.digits_.length(); i-- > 0;)
	{
		for (size_t j = 0; j < 16; j++)
		{
			int compare_result = LN::comparator(num2 * result, num1, false);
			if (compare_result == 1)
			{
				break;
			}
			result.digits_[i]++;
		}
		result.digits_[i]--;
	}
	LN::remove_leading_zeros(result);
	return result;
}

// Operator of taking the remainder
LN operator%(const LN &num1, const LN &num2)
{
	LN result = num1 - (num1 / num2) * num2;
	return result;
}

// Addition and assignment operator
LN &LN::operator+=(const LN &num)
{
	return *this = (*this + num);
}

// Subtraction and assignment operator
LN &LN::operator-=(const LN &num)
{
	return *this = (*this - num);
}

// Multiplication and assignment operator
LN &LN::operator*=(const LN &num)
{
	return *this = (*this * num);
}

// Division and assignment operator
LN &LN::operator/=(const LN &num)
{
	return *this = (*this / num);
}

// Operator of taking the remainder and assignment
LN &LN::operator%=(const LN &num)
{
	return *this = (*this % num);
}

// Less operator
bool operator<(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return compare_result == 0;
}

// Less and equality operator
bool operator<=(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return (compare_result == 0 || compare_result == -1);
}

// More operator
bool operator>(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return compare_result == 1;
}

// More and equality operator
bool operator>=(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return (compare_result == 1 || compare_result == -1);
}

// Equality operator
bool operator==(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return compare_result == -1;
}

// Inequality operator
bool operator!=(const LN &num1, const LN &num2)
{
	int compare_result = LN::comparator(num1, num2, true);
	return compare_result != -1;
}

// Conversion operator to ll
LN::operator long long()
{
	if (this->NaN_)
	{
		return NAN;
	}
	if (*this < LN(LLONG_MIN) || LN(LLONG_MAX) < *this)
	{
		throw std::runtime_error("Conversion is not possible! long long does not contain the specified LN");
	}
	long long ratio = 1;
	long long result = 0;
	for (size_t i = 0; i < this->digits_.length(); i++, ratio *= 16)
	{
		result += this->digits_[i] * ratio;
	}
	return this->sign_ ? -result : result;
}

// Conversion operator to bool
LN::operator bool() const
{
	if (this->NaN_)
	{
		return NAN;
	}
	int compare_result = LN::comparator(*this, 0_ln, false);
	return compare_result == -1;
}

// Literal operator
LN operator""_ln(const char *num)
{
	return LN(num);
}

// Auxiliary number output function
void LN::to_string(std::ostream &f_out)
{
	if (NaN_)
	{
		f_out << "NaN\n";
		return;
	}
	if (LN::comparator(*this, 0_ln, false) == -1)
	{
		f_out << "0\n";
		return;
	}
	if (sign_)
	{
		f_out << '-';
	}
	for (size_t i = digits_.length(); i-- > 0;)
	{
		f_out << uint_to_char(digits_[i]);
	}
	f_out << '\n';
}

// Auxiliary char to uint8_t translation function
uint8_t LN::char_to_uint(const char ch)
{
	if ('a' <= ch && ch <= 'f')
	{
		return ch - 'a' + 10;
	}
	if ('A' <= ch && ch <= 'F')
	{
		return ch - 'A' + 10;
	}
	return ch - '0';
}

// Auxiliary uint8_t to char translation function
char LN::uint_to_char(const uint8_t uint)
{
	if (10 <= uint && uint <= 15)
	{
		return (char)(uint + 'A' - 10);
	}
	return (char)(uint + '0');
}

// Auxiliary function for removing leading zeros
void LN::remove_leading_zeros(LN &num)
{
	while (!num.digits_.back() && num.digits_.length() > 1)
	{
		num.digits_.pop_back();
	}
}

// Auxiliary addition function
LN LN::add(const LN &num1, const LN &num2, bool s)
{
	LN result;
	result.digits_.pop_back();
	if (num1.NaN_ || num2.NaN_)
	{
		result.NaN_ = true;
		return result;
	}
	result.sign_ = s;
	size_t num1_length = num1.digits_.length();
	size_t num2_length = num2.digits_.length();
	uint8_t transitional_digit = 0;
	for (size_t i = 0; i < std::min(num1_length, num2_length); i++)
	{
		uint8_t current_digit = num1.digits_[i] + num2.digits_[i] + transitional_digit;
		result.digits_.push_back(current_digit % 16);
		transitional_digit = current_digit / 16;
	}
	for (size_t i = std::min(num1_length, num2_length); i < std::max(num1_length, num2_length); i++)
	{
		uint8_t current_digit = num1_length > num2_length ? num1.digits_[i] + transitional_digit : num2.digits_[i] + transitional_digit;
		result.digits_.push_back(current_digit % 16);
		transitional_digit = current_digit / 16;
	}
	if (transitional_digit != 0)
	{
		result.digits_.push_back(transitional_digit);
	}
	return result;
}

// Auxiliary subtraction function
LN LN::subtract(const LN &num1, const LN &num2, bool s)
{
	LN result;
	result.digits_.pop_back();
	if (num1.NaN_ || num2.NaN_)
	{
		result.NaN_ = true;
		return result;
	}
	result.sign_ = s;
	uint8_t transitional_digit = 0;
	for (size_t i = 0; i < num2.digits_.length(); i++)
	{
		int current_digit = num1.digits_[i] - num2.digits_[i] - transitional_digit;
		if (current_digit < 0)
		{
			current_digit += 16;
			transitional_digit = 1;
		}
		else
		{
			transitional_digit = 0;
		}
		result.digits_.push_back((uint8_t)current_digit);
	}
	for (size_t i = num2.digits_.length(); i < num1.digits_.length(); i++)
	{
		if (num1.digits_[i] - transitional_digit < 0)
		{
			result.digits_.push_back(num1.digits_[i] - transitional_digit + 16);
			transitional_digit = 1;
		}
		else
		{
			result.digits_.push_back(num1.digits_[i] - transitional_digit);
			transitional_digit = 0;
		}
	}
	LN::remove_leading_zeros(result);
	return result;
}

// Auxiliary comparison function
int LN::comparator(const LN &num1, const LN &num2, bool s)
{
	if (num1.NaN_ || num2.NaN_)
	{
		return -2;
	}
	if (num1.sign_ != num2.sign_ && s)
	{
		return num1.sign_ < num2.sign_;
	}
	size_t num1_length = num1.digits_.length();
	size_t num2_length = num2.digits_.length();
	if (num1_length != num2_length)
	{
		if (num1.sign_ && s)
		{
			return num1_length < num2_length;
		}
		else
		{
			return num1_length > num2_length;
		}
	}
	for (size_t i = num1_length; i-- > 0;)
	{
		if (num1.digits_[i] != num2.digits_[i])
		{
			if (num1.sign_ && s)
			{
				return num1.digits_[i] < num2.digits_[i];
			}
			else
			{
				return num1.digits_[i] > num2.digits_[i];
			}
		}
	}
	return -1;
}
