#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <iostream>

class Number
{
  public:
	~Number() { clear(); }

	Number &operator=(const Number &digits)
	{
		if (this != &digits)
		{
			size_ = 0;
			for (size_t i = 0; i < digits.length(); i++)
			{
				push_back(digits[i]);
			}
		}
		return *this;
	}

	uint8_t operator[](size_t index) const { return elements_[index]; }

	uint8_t &operator[](size_t index) { return elements_[index]; }

	void push_back(const uint8_t element)
	{
		ensure_capacity(size_ + 1);
		elements_[size_++] = element;
	}

	uint8_t pop_back()
	{
		size_--;
		const uint8_t result = elements_[size_];
		elements_[size_] = 0;
		return result;
	}

	uint8_t back() { return elements_[size_ - 1]; }

	size_t length() const { return size_; }

	void fill(size_t s, const uint8_t element)
	{
		size_ = 0;
		for (size_t i = 0; i < s; i++)
		{
			push_back(element);
		}
	}

	void clear()
	{
		if (elements_)
		{
			free(elements_);
			elements_ = nullptr;
		}
	}

  private:
	size_t size_ = 0;
	size_t capacity_ = 0;
	uint8_t *elements_{};

	void ensure_capacity(size_t s)
	{
		if (capacity_ == 0)
		{
			elements_ = (uint8_t *)malloc(sizeof(uint8_t));
			if (elements_ == nullptr)
			{
				throw std::runtime_error("Memory allocation error! Not enough memory for further work.\n");
			}
			capacity_ = 1;
		}
		if (capacity_ < s)
		{
			uint8_t *saved_elements = elements_;
			elements_ = (uint8_t *)realloc(elements_, capacity_ * 2);
			if (elements_ == nullptr)
			{
				free(saved_elements);
				throw std::runtime_error("Memory allocation error! Not enough memory for further work.\n");
			}
			capacity_ *= 2;
		}
	}
};
