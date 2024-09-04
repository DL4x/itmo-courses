#include "LN.h"
#include "return_codes.h"

#include <fstream>
#include <iostream>
#include <set>
#include <stack>

std::set< std::string > unary = { "_", "~" };
std::set< std::string > binary = { "+", "-", "*", "/", "%", "<", "<=", ">", ">=", "==", "!=" };

LN unary_operation(std::string &operation, LN &num)
{
	if (operation == "_")
	{
		return -num;
	}
	else
	{
		return ~num;
	}
}

LN binary_operation(std::string &operation, LN &num1, LN &num2)
{
	if (operation == "+")
	{
		return num1 + num2;
	}
	else if (operation == "-")
	{
		return num1 - num2;
	}
	else if (operation == "*")
	{
		return num1 * num2;
	}
	else if (operation == "/")
	{
		return num1 / num2;
	}
	else if (operation == "%")
	{
		return num1 % num2;
	}
	else if (operation == "<")
	{
		return num1 < num2 ? 1_ln : 0_ln;
	}
	else if (operation == "<=")
	{
		return num1 <= num2 ? 1_ln : 0_ln;
	}
	else if (operation == ">")
	{
		return num1 > num2 ? 1_ln : 0_ln;
	}
	else if (operation == ">=")
	{
		return num1 >= num2 ? 1_ln : 0_ln;
	}
	else if (operation == "==")
	{
		return num1 == num2 ? 1_ln : 0_ln;
	}
	else
	{
		return num1 != num2 ? 1_ln : 0_ln;
	}
}

int main(int argc, char **argv)
{
	if (argc != 3)
	{
		std::cerr << "Invalid number of arguments! The arguments must contain the names of the input and output "
					 "files.\n";
		return ERROR_PARAMETER_INVALID;
	}
	std::ifstream f_in(argv[1]);
	if (!f_in.is_open())
	{
		std::cerr << "Error opening the input file! Check the correctness of the input file and the specified path.\n";
		return ERROR_CANNOT_OPEN_FILE;
	}
	std::string line;
	std::stack< LN > stack;
	try
	{
		while (f_in >> line)
		{
			if (unary.find(line) != unary.end())
			{
				LN temp = stack.top();
				stack.pop();
				stack.push(unary_operation(line, temp));
			}
			else if (binary.find(line) != binary.end())
			{
				LN temp1 = stack.top();
				stack.pop();
				LN temp2 = stack.top();
				stack.pop();
				stack.push(binary_operation(line, temp1, temp2));
			}
			else
			{
				stack.emplace(line);
			}
		}
	} catch (const std::runtime_error &error)
	{
		f_in.close();
		std::cerr << error.what();
		return ERROR_OUT_OF_MEMORY;
	}
	f_in.close();

	std::ofstream f_out(argv[2]);
	if (!f_out.is_open())
	{
		std::cerr << "Error opening the output file! Check the correctness of the output file and the specified "
					 "path.\n";
		return ERROR_CANNOT_OPEN_FILE;
	}
	while (!stack.empty())
	{
		stack.top().to_string(f_out);
		stack.pop();
	}
	f_out.close();
	return SUCCESS;
}
