function drawHistogram(img)

    close all;
    
    %[pixelCount, grayLevels] = imhist(img);
    %subplot(3, 3, 2);
    %bar(pixelCount);
    %title('Histogram of original image', 'FontSize', captionFontSize);
    %xlim([0 grayLevels(end)]); % Scale x axis manually.
    
    imhist(img);
    grid on;
end
