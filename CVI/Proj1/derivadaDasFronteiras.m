function derivadaDasFronteiras(img1)
    close all
    
    %Step 2: Threshold the Image
    I = rgb2gray(img1);
    bw = imbinarize(I);
    imshow(bw)

    %Step 3: remove the noise

    % remove all object containing fewer than 30 pixels
    bw = bwareaopen(bw,70);

    % fill a gap in the pen's cap
    se = strel('disk',2);
    bw = imclose(bw,se);

    % fill any holes, so that regionprops can be used to estimate
    % the area enclosed by each of the boundaries
    bw = imfill(bw,'holes');

    imshow(bw)
    
    
   %Step 4: find the boundaries 
    [B,L] = bwboundaries(bw,'holes');

    % Display the label matrix and draw each boundary
    imshow(label2rgb(L, @jet, [.5 .5 .5]))
    hold on
    for k = 1:length(B)
      boundary = B{k};
      plot(boundary(:,2), boundary(:,1), 'w', 'LineWidth', 2)
    end
    
    %Step 5: Determine which Objects are Round
    stats = regionprops(L,'Area','Centroid');

    threshold = 0.84;

    % loop over the boundaries
    for k = 1:length(B)

      % obtain (X,Y) boundary coordinates corresponding to label 'k'
      boundary = B{k};

      % compute a simple estimate of the object's perimeter
      delta_sq = diff(boundary).^2;    
      perimeter = sum(sqrt(sum(delta_sq,2)));

      % obtain the area calculation corresponding to label 'k'
      area = stats(k).Area;

      % compute the roundness metric
      metric = 4*pi*area/perimeter^2;

      % display the results
      metric_string = sprintf('%2.2f',metric);

      % mark objects above the threshold with a black circle
      if metric > threshold
        centroid = stats(k).Centroid;
        plot(centroid(1),centroid(2),'ko');
      end

      text(boundary(1,2)-35,boundary(1,1)+13,metric_string,'Color','red',...
           'FontSize',14,'FontWeight','bold');

    end

    title('Closer to 1 = more round. Black circle on the centroid = coin');
    

end
