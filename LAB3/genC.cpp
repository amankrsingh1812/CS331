#include<bits/stdc++.h>
using namespace std;

int main()
{
	int t=10;
	while(t--)
	{
		int n;
		if(t>=5)
			n=(rand() % 20) + 1;
		else
			n=(rand()%50)+1;
		cout<<"qsort [";
		while(n--)
		{
			cout<<rand()%1000-300;
			if(n!=0)
				cout<<",";
		}
		cout<<"]\n";
	}
}
