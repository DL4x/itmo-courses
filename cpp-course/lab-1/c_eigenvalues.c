#include "return_codes.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>

void clear(int index, double **array);

double **create(int n);

// Functions for QR-algorithm
double norm(int n, int index, double vector[])
{
	double result = 0;
	for (int i = index; i < n; i++)
	{
		result += pow(vector[i], 2);
	}
	return sqrt(result);
}

double **identity_matrix(int n)
{
	double **result = create(n);
	if (result == NULL)
	{
		return NULL;
	}
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			result[i][j] = (i == j);
		}
	}
	return result;
}

double **householder(int n, int index, double vector[])
{
	double *vector_u = (double *)malloc(n * sizeof(double));
	if (vector_u == NULL)
	{
		return NULL;
	}
	for (int i = 0; i < n; i++)
	{
		vector_u[i] = i == index ? vector[i] - norm(n, index, vector) : vector[i];
	}
	double **result = create(n);
	if (result == NULL)
	{
		free(vector_u);
		return NULL;
	}
	const double norma = norm(n, index, vector_u);
	for (int j = 0; j < n; j++)
	{
		for (int i = 0; i < n; i++)
		{
			if (j < index || i < index || norma == 0)
			{
				result[i][j] = (i == j);
			}
			else
			{
				result[i][j] = (i == j) - (2 * (vector_u[j] * vector_u[i]) / pow(norma, 2));
			}
		}
	}
	free(vector_u);
	vector_u = NULL;
	return result;
}

double **matrix_multiply(int n, double **matrix1, double **matrix2)
{
	double **result = create(n);
	if (result == NULL)
	{
		return NULL;
	}
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			result[i][j] = 0;
			for (int k = 0; k < n; k++)
			{
				result[i][j] += matrix2[i][k] * matrix1[k][j];
			}
		}
	}
	return result;
}

double **qr_algorithm(int n, double **matrix)
{
	double **matrix_q = identity_matrix(n);
	if (matrix_q == NULL)
	{
		return NULL;
	}
	double **matrix_r = matrix;
	double **matrix_p = NULL;
	for (int i = 0; i < n - 1; i++)
	{
		matrix_p = householder(n, i, matrix_r[i]);
		if (matrix_p == NULL)
		{
			goto Clear;
		}
		matrix_q = matrix_multiply(n, matrix_q, matrix_p);
		if (matrix_q == NULL)
		{
			goto Clear;
		}
		matrix_r = matrix_multiply(n, matrix_p, matrix_r);
		if (matrix_r == NULL)
		{
			goto Clear;
		}
		clear(n, matrix_p);
	}
	double **result = matrix_multiply(n, matrix_r, matrix_q);
	if (result == NULL)
	{
		goto Clear;
	}
	clear(n, matrix_q);
	matrix_q = NULL;
	clear(n, matrix_r);
	matrix_r = NULL;
	return result;

Clear:
	if (matrix_q)
	{
		clear(n, matrix_q);
		matrix_q = NULL;
	}
	if (matrix_r)
	{
		clear(n, matrix_r);
		matrix_r = NULL;
	}
	if (matrix_p)
	{
		clear(n, matrix_p);
		matrix_p = NULL;
	}
	return NULL;
}
// Functions for QR-algorithm

int main(int argc, char **argv)
{
	int result = SUCCESS;
	double **matrix = NULL;
	FILE *f_in = NULL;
	FILE *f_out = NULL;

	// Input data
	if (argc != 3)
	{
		fprintf(stderr, "Invalid number of arguments! The arguments must contain the names of the input and output files.\n");
		result = ERROR_PARAMETER_INVALID;
		goto Clear;
	}
	f_in = fopen(argv[1], "r");
	if (f_in == NULL)
	{
		fprintf(stderr, "Error opening the input file! Check the correctness of the input file and the specified path.\n");
		result = ERROR_CANNOT_OPEN_FILE;
		goto Clear;
	}
	int n;
	fscanf(f_in, "%i", &n);
	matrix = create(n);
	if (matrix == NULL)
	{
		fprintf(stderr, "Memory allocation error! Not enough memory for further work.\n");
		result = ERROR_OUT_OF_MEMORY;
		goto Clear;
	}
	for (int j = 0; j < n; j++)
	{
		for (int i = 0; i < n; i++)
		{
			fscanf(f_in, "%lf", &matrix[i][j]);
		}
	}
	fclose(f_in);
	// Input data

	// QR-algorithm
	for (int i = 0; i < 500; i++)
	{
		matrix = qr_algorithm(n, matrix);
		if (matrix == NULL)
		{
			fprintf(stderr, "Memory allocation error! Not enough memory for further work.\n");
			result = ERROR_OUT_OF_MEMORY;
			goto Clear;
		}
	}
	// QR-algorithm

	// Output data
	f_out = fopen(argv[2], "w");
	if (f_out == NULL)
	{
		fprintf(stderr, "Error opening the output file! Check the correctness of the output file and the specified path.\n");
		clear(n, matrix);
		matrix = NULL;
		result = ERROR_CANNOT_OPEN_FILE;
		goto Clear;
	}
	const double eps = 1.0 / 500;
	for (int i = 0; i < n; i++)
	{
		if (fabs(matrix[i][i + 1]) > eps && i != n - 1)
		{
			double sum_11_22 = matrix[i][i] + matrix[i + 1][i + 1];
			double mul_11_22 = matrix[i][i] * matrix[i + 1][i + 1];
			double mul_12_21 = matrix[i][i + 1] * matrix[i + 1][i];
			double discriminant = pow(sum_11_22, 2) - 4 * (mul_11_22 - mul_12_21);
			fprintf(f_out, "%g +%gi\n", sum_11_22 / 2, sqrt(fabs(discriminant)) / 2);
			fprintf(f_out, "%g -%gi\n", sum_11_22 / 2, sqrt(fabs(discriminant)) / 2);
			i++;
		}
		else
		{
			fprintf(f_out, "%g\n", matrix[i][i]);
		}
	}
	fclose(f_out);
	clear(n, matrix);
	matrix = NULL;
	return result;
	// Output data

Clear:
	if (f_in)
	{
		fclose(f_in);
	}
	if (f_out)
	{
		fclose(f_out);
	}
	return result;
}

// Useful functions
double **create(int n)
{
	double **matrix = (double **)malloc(n * sizeof(double *));
	if (matrix == NULL)
	{
		return NULL;
	}
	for (int i = 0; i < n; i++)
	{
		matrix[i] = (double *)malloc(n * sizeof(double));
		if (matrix[i] == NULL)
		{
			clear(i, matrix);
			matrix = NULL;
			return NULL;
		}
	}
	return matrix;
}

void clear(int index, double **array)
{
	for (int i = 0; i < index; i++)
	{
		free(array[i]);
	}
	free(array);
}
// Useful functions
