close all;
clear all;

% Data input and parse to t, x, y, z axis, 
m = csvread('pure_data.csv');
x = m(:,2);
y = m(:,3);
z = m(:,4);
t = m(:,1);
mag =sqrt(x.^2+y.^2+z.^2);
subplot(2,1,1);
plot(t, mag);
total_size = size(t,1);

% Defination
alpha = 0.1;
smoothed = zeros(total_size,1);
smoothed(1) = mag(1);
window_size = 180;

% Loop through the data and process
for i=2:total_size
    smoothed(i) = alpha*mag(i) + (1-alpha)*smoothed(i-1);
end

subplot(2,1,2);
plot(t, smoothed);
hold on;

for j=1:window_size:total_size
    if j+window_size-1>total_size
        break;
    end
    data_buffer = smoothed(j:j+window_size-1);
    min_val = min(data_buffer);
    max_val = max(data_buffer);
    avg_val = (max_val+min_val)/2;
    plot([t(j) t(j+window_size-1)], [min_val min_val],'r');
    plot([t(j) t(j+window_size-1)], [max_val max_val],'r');
    plot([t(j) t(j+window_size-1)], [avg_val avg_val],'g');
    
    data_buffer_avg = data_buffer - avg_val;
    aboveZero = data_buffer_avg > 0;
    zeroCrossing = diff(aboveZero) == 1;
    zeroCrossingIndex = find(zeroCrossing);
    step_size = size(zeroCrossingIndex,1);
    plot(t(zeroCrossingIndex+j), ones(step_size,1)*avg_val ,'y*');
    
end
% heuristic: these is lower and up limit for step speed







