% build_gaussian_filter_1d - compute a Gaussian filter.
%
%   f = build_gaussian_filter_1d(n,s,N);
%
%   Copyright (c) 2004 Gabriel Peyr?

function f = build_gaussian_filter_1d(n,s,N)

if nargin<2
    error('Not enough arguments.');
end
if nargin<3
    N = n;
end

if s<=0
    f = zeros(n,1);
    f(round((n-1)/2)) = 1;
    return;
end

x = ( (0:n-1)-(n-1)/2 )/(N-1);
f = exp( -x.^2/(2*s^2) );
f = f / sum(f(:));