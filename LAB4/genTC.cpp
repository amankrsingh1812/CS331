#include<bits/stdc++.h>
using namespace std;

int main()
{
	int t = 10;
	while (t--)
	{
		int n = 10 + rand() % 6;
		for (int i = 1; i <= n; i++)
		{
			cout << (rand() % 100)+1;
			if (i == n)
				cout << "\n";
			else cout << ",";
		}
	}
}
