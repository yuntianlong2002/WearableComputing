function CS390MB_Smoothing_Filtering_Denoising()

%% Initialize signals and variables
AWalk15 = load('Treadmill_walk_1.5mph_pocket.csv');
AJog55 = load('Treadmill_jog_5.5mph_pocket.csv');

f1_orig = AWalk15(1000:2023,1);
f2_orig = AJog55(1000:2023,1);

n = 1024;

sigma = .1;
f1 = f1_orig + randn(n,1)*sigma;
f2 = f2_orig+ randn(n,1)*sigma;

%% Visualize the x, y, z for accelerometer
figure(1);
clf;
subplot(3,1,1);
plot(AWalk15(:,1)); axis([1 length(AWalk15(:,1)) -0.5 0.5]);
title('Signal 1: Accelerometer (X)','FontSize',16);
subplot(3,1,2);
plot(AWalk15(:,2),'g'); axis([1 length(AWalk15(:,2)) -0.5 0.5]);
title('Signal 1: Accelerometer (Y)','FontSize',16);
subplot(3,1,3);
plot(AWalk15(:,3)); axis([1 length(AWalk15(:,3)) -0.5 0.5]);
title('Signal 1: Accelerometer (Z)','FontSize',16);

%% Visualize the two signals
figure(1);
clf;
subplot(2,1,1);
plot(f1_orig); axis([1 n -0.5 0.5]);
title('Signal 1: Treadmill walk at 1.5mph','FontSize',16);
subplot(2,1,2);
plot(f2_orig,'g'); axis([1 n -3 3]);
title('Signal 2: Treadmill jog at 5.5mph','FontSize',16);


%% Adding noise to signal
figure(2);
clf;
subplot(3,1,1);
plot(f1_orig); axis([1 n -0.5 0.5]);
title('Clean Signal 1','FontSize',16);
subplot(3,1,2);
plot(f1-f1_orig,'g'); axis([1 n -3*sigma 3*sigma]);
title('Noise','FontSize',16);
subplot(3,1,3);
plot(f1,'r'); axis([1 n -0.5 0.5]);
title('Noisy Signal 1','FontSize',16);

%% Gaussian filter: build filter, show gaussian filtered signal, and show result with different parameters

% mu is width of the filter, approximate
mu = 5;
m = 101; % total size of the filter, odd number is better
h = build_gaussian_filter_1d(m,mu/(4*n),n);

% Fourier transform of the (centered) filter
hf = real( fft(fftshift(h)) );
hf = fftshift(hf);

% plot the time-domain gaussian filter
figure(2); clf;
subplot(2,1,1);
t = (-length(h)/2:length(h)/2-1)';
plot(t,h); axis('tight');
title('Gaussian filter','FontSize',16);
subplot(2,1,2);
plot( t,hf,'g'); axis('tight');
title('Fourier transform of Gaussian filter','FontSize',16);

% plot signal smoothed using a gaussian filter
figure(3); clf;
subplot(3,1,1);
plot(f1,'b'); axis([1 n -0.5 0.5]);
title('Noisy Signal 1','FontSize',16);
subplot(3,1,2);
plot(f1_orig); axis([1 n -0.5 0.5]);
title('Original - Clean signal','FontSize',16);
subplot(3,1,3);
plot(perform_convolution(f1,h),'r'); axis([1 n -0.5 0.5]);
title('Gaussian Filtered Signal 1','FontSize',16);

% compute the error (compared to the original signal) for different choices
% of mu.
mu_list = linspace( 0, 10, 6);
err = [];
for i = 1:length(mu_list)
    mu = mu_list(i);
    h = build_gaussian_filter_1d(m,mu/(4*n),n);
    fnh(i,:) = perform_convolution(f1,h);
    % compute the error
    e = sum( (f1_orig(:)-fnh(i,:)').^2 );
    err = [ err, e ];
end

% display smoothed signal for different choices of mu
figure(4); clf;
for i = 1:length(mu_list)
    subplot(3,2,i);
    plot(fnh(i,:),'r'); axis([1 n -0.5 0.5]);
    title(sprintf('Signal 1: mu = %0.1f',mu_list(i)),'FontSize',16);
end

% Display the evolution of the oracle denoising error  as a function of mu.
% Set  to the value of the optimal parameter.
figure(18); clf;
plot(mu_list,err);
set(gca,'FontSize',15);
title('Denoising error','FontSize',16);
xlabel('Signal 1: \mu','FontSize',16);
ylabel('|original - denoised|','FontSize',16);

%% Wavelet denoising demo. Follow the following commands in matlab (Octave does not have a wavelet toolbox)
%In matlab, type: wavemenu Choose: SWT denoising 1d load AWalk15.mat Choose
%unnormalized white noise and normalized white noise options Click on
%Denoise. play around with the different thresholds at the individual
%levels.
figure(5); clf;
wfs = load('WaveletFS.mat');
subplot(2,1,1);
plot(f1_orig,'b'); axis([1 n -0.5 0.5]);
title('Original Signal 1','FontSize',16);
subplot(2,1,2);
plot(wfs.wfs,'r'); axis([1 n -0.5 0.5]);
title('Wavelet Scaled Signal 1','FontSize',16);

%% Moving average smoothing.

% moving average over 5 values and 9 values
figure(6); clf;
subplot(3,1,1);
plot(f1_orig,'r'); axis([1 n -0.5 0.5]);set(gca,'FontSize',15);
title('Original Signal 1','FontSize',16);
subplot(3,1,2);
plot(mvsmooth(f1,5),'r'); axis([1 n -0.5 0.5]);
title('MA - 5 values','FontSize',16);
subplot(3,1,3);
plot(mvsmooth(f1,9),'r'); axis([1 n -0.5 0.5]);
title('MA - 9 values','FontSize',16);


%% Exponential smoothing

% exponential average for different choices of alpha
alpha_list = [0 0.01 0.1 0.3 0.6 0.9];
figure(7); clf;
subplot(3,2,1);
set(gca,'FontSize',15);
plot(f1_orig,'g'); axis([1 n -0.5 0.5]);
title('Original Signal 1','FontSize',16);
for i = 2:length(alpha_list)
    subplot(3,2,i);
    set(gca,'FontSize',15);
    plot(expsmooth(f1,alpha_list(i)),'g'); axis([1 n -0.5 0.5]);
    title(sprintf('alpha = %0.1f',alpha_list(i)),'FontSize',16);
end

%% Overall comparison of different schemes
% Overall comparison of schemes
figure(10); clf;
set(gca,'FontSize',15);
subplot(2,2,1);
plot(f1_orig,'r'); axis([1 n -0.5 0.5]);
title('Original Signal 1','FontSize',16);
subplot(2,2,2);
plot(mvsmooth(f1,5),'r'); axis([1 n -0.5 0.5]);
title('MA - 5 values','FontSize',16);
subplot(2,2,3);
plot(fnh(3,:),'r'); axis([1 n -0.5 0.5]);
title('Gaussian Filtered Signal 1','FontSize',16);
subplot(2,2,4);
plot(wfs.wfs,'r'); axis([1 n -0.5 0.5]);
title('Wavelet Scaled Signal 1','FontSize',16);


%% Signal 2: Going through the gaussian filtering process
figure(11);
clf;
set(gca,'FontSize',15);
subplot(2,1,1);
plot(f1); axis([1 n -0.5 0.5]);
title('Signal 1','FontSize',16);
subplot(2,1,2);
plot(f2,'r'); axis([1 n -3 3]);
title('Signal 2','FontSize',16);

mu_list = linspace( 0, 10, 6);
err = [];
for i = 1:length(mu_list)
    mu = mu_list(i);
    h = build_gaussian_filter_1d(m,mu/(4*n),n);
    fnh(i,:) = perform_convolution(f2,h);
    % compute the error
    e = sum( (f2_orig(:)-fnh(i,:)').^2 );
    err = [ err, e ];
end

figure(12); clf;
for i = 1:length(mu_list)
    subplot(3,2,i);
    plot(fnh(i,:),'r'); axis([1 n -3 3]);
    title(sprintf('Signal 2: mu = %0.1f',mu_list(i)),'FontSize',16);
end

% Display the evolution of the oracle denoising error  as a function of .
% Set  to the value of the optimal parameter.
figure(1); clf;
plot(mu_list,err);
set(gca,'FontSize',15);
title('Denoising error','FontSize',16);
xlabel('Signal 2: \mu','FontSize',16);
ylabel('|original - denoised|','FontSize',16);

% figure(18); clf; plot(0.01:0.01:1,denerr); set(gca,'FontSize',15);
% title('Denoising error','FontSize',16); xlabel('\mu','FontSize',20);
% ylabel('|original - denoised|','FontSize',20);
%

% figure(13); clf; subplot(2,1,1); plot(fnh(:,3),'r'); axis([1 n -0.5
% 0.5]); title('Gaussian Filtered Signal 1','FontSize',16); subplot(2,1,2);
% plot( denoise(f2,2),'r'); axis([1 n -2 2]); title('Gaussian Filtered
% Signal 2','FontSize',16);

end
