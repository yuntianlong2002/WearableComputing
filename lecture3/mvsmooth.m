
function s = mvsmooth(data, taps)
% Calculates moving average with specified taps - must be odd

n = length(data);
s = data;
if mod(taps,2)==0
    llim = (taps/2-1);
    ulim = taps/2;
else
    llim = (taps-1)/2;
    ulim = (taps-1)/2;
end
for i = round(taps/2):n - round(taps/2)
    s(i) = mean(data(i-llim:i+ulim));
end
end
