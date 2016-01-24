
function s = expsmooth(data, alpha)
% Calculates single exponentially smoothed data with weight parameter
% alpha.

n = length(data);
s = zeros(1,n+1);
s(2) = data(1);
for i = 3:n+1
    s(i) = alpha*data(i-1) + (1-alpha)*s(i-1);
end
end
