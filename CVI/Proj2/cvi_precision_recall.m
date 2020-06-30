clear all;

fid = fopen('2015-04-22-18-22-35_tase.gt.txt');
nline = fgets(fid);
thr_global = 70;
thr_dif = 15;
min_area = 400;
max_area = 1100;
se = strel('disk',5);
%se = strel('disk',2);      %melhor para o inicio do video

Precision = [];
Recall = [];

while ischar(nline)
     line_splitted = strsplit(nline);
     frame_id = line_splitted(1,1);
     top_left_labels = [str2double(line_splitted(1,2)),str2double(line_splitted(1,3))];
     vessel_area = [str2double(line_splitted(1,4)),str2double(line_splitted(1,5))];
     f = str2double(frame_id);
     frame = imread(sprintf('frames/frame-%.d.tif',f)); 
     imshow(frame);
     rectangle('Position',[top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], 'EdgeColor',[1 0 0],'linewidth', 2 );
     
     nline1 = fgets(fid);
     if f < 7550
        line_splitted1 = strsplit(nline1);
        top_left_labels1 = [str2double(line_splitted1(1,2)),str2double(line_splitted1(1,3))];
        vessel_area1 = [str2double(line_splitted1(1,4)),str2double(line_splitted1(1,5))];
        rectangle('Position',[top_left_labels1(1,1) top_left_labels1(1,2) vessel_area1(1,1) vessel_area1(1,2)], 'EdgeColor',[1 0 0],'linewidth', 2 );
     end
     
     
     %------------------------VESSEL-DETECTION----------------------------
     mask = (frame(:,:,1) > thr_global) .* ... 
            (abs(frame(:,:,2)- frame(:,:,1)) < thr_dif) .* ... 
            (abs(frame(:,:,3) - frame(:,:,1)) < thr_dif);
        
    mask = imdilate(mask,se);   
    %imshow(mask);  
    bw = mask ;
    %bw = bwareaopen(bw,150);
    
    %--------------------------SPATIAL-VAILIDATION-------------------------
    [lb num]=bwlabel(bw);
    regionProps = regionprops(lb,'area','FilledImage','Centroid','MajorAxisLength');  %adicionar raio
    inds = find([regionProps.Area]>min_area & [regionProps.Area]< max_area);
    
    num_regions = length(inds);
    
    if num_regions
        for j=1:num_regions
            [lin col]= find(lb == inds(j));
            upLPoint = min([lin col]);
            dWindow  = max([lin col]) - upLPoint + 1;
            if ( f < 7650 & lin > 100 & lin < 300 & col > 150 & col < 350)
                small = [top_left_labels1(1,1),top_left_labels1(1,2);upLPoint(2),upLPoint(1)];
                big = [top_left_labels(1,1),top_left_labels(1,2);upLPoint(2),upLPoint(1)];
                d_small = pdist(small,'euclidean');
                d_big = pdist(big,'euclidean');
                if (d_small >= d_big )
                    groundTruth = [top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)];        %nossa frame depois de labelling
                    algorithmDetection = [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)];                               %zona detectada pelo algoritmo
                    GT = vessel_area(1,1)*vessel_area(1,2);                                                             %area do groundTruth
                    AD = dWindow(2)*dWindow(1);                                                                         %area do algorithmDetection
                    TruePositive = rectint(groundTruth, algorithmDetection);                                            %interesction
                    FalsePositive = AD - TruePositive;
                    FalseNegative = GT - TruePositive;
                    Precision = [Precision TruePositive/(TruePositive+FalsePositive)];
                    Recall = [Recall TruePositive/(TruePositive+FalseNegative)];
                    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                else
                    groundTruth = [top_left_labels1(1,1) top_left_labels1(1,2) vessel_area1(1,1) vessel_area1(1,2)];    %nossa frame depois de labelling
                    algorithmDetection = [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)];                               %zona detectada pelo algoritmo
                    GT = vessel_area1(1,1)*vessel_area1(1,2);                                                           %area do groundTruth
                    AD = dWindow(2)*dWindow(1);                                                                         %area do algorithmDetection
                    TruePositive = rectint(groundTruth, algorithmDetection);                                            %interesction
                    FalsePositive = AD - TruePositive;
                    FalseNegative = GT - TruePositive;
                    Precision = [Precision TruePositive/(TruePositive+FalsePositive)];
                    Recall = [Recall TruePositive/(TruePositive+FalseNegative)];
                    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                end
           elseif (f >= 7650 & f<8000 & lin > 100 & lin < 350 & col > 100 & col < 350)
                groundTruth = [top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)];      %nossa frame depois de labelling
                algorithmDetection = [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)];                   %zona detectada pelo algoritmo
                GT = vessel_area(1,1)*vessel_area(1,2);                                               %area do groundTruth
                AD = dWindow(2)*dWindow(1);                                                             %area do algorithmDetection
                TruePositive = rectint(groundTruth, algorithmDetection);                                %interesction
                FalsePositive = AD - TruePositive;
                FalseNegative = GT - TruePositive;
                Precision = [Precision TruePositive/(TruePositive+FalsePositive)];
                Recall = [Recall TruePositive/(TruePositive+FalseNegative)];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
            elseif (f > 8000 & lin > 50 & lin < 400 & col > 100 & col < 550)
                groundTruth = [top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)];      %nossa frame depois de labelling
                algorithmDetection = [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)];                   %zona detectada pelo algoritmo
                GT = vessel_area(1,1)*vessel_area(1,2);                                               %area do groundTruth
                AD = dWindow(2)*dWindow(1);                                                             %area do algorithmDetection
                TruePositive = rectint(groundTruth, algorithmDetection);                                %interesction
                FalsePositive = AD - TruePositive;
                FalseNegative = GT - TruePositive;
                Precision = [Precision TruePositive/(TruePositive+FalsePositive)];
                Recall = [Recall TruePositive/(TruePositive+FalseNegative)];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
            else
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[1 1 1],'linewidth',2);
            end
        end
    end
   
    nline = fgets(fid);
    drawnow;
end
fclose(fid);

%----------------------------GRAPH-Pre-Rec---------------------------------

vector1a = [];
vector2a = [];
vector1b = [];
vector2b = [];
for k=1:length(Precision)
    if k <= 301
        vector1a = [vector1a Precision(k)];
        vector1b = [vector1b Recall(k)];
    else
        vector2a = [vector2a Precision(k)];
        vector2b = [vector2b Recall(k)];
    end
end

x = linspace(0,length(vector1a), length(vector1a));

figure(2)
y = sort(vector1a,'descend');
y0 = sort(vector1b,'descend');
plot(y,x,y0,x);
legend({'Precision','Recall'});
title('Precision and Recall');
xlabel('Intersection Rates');
ylabel('Number of Regions');

x = linspace(0,length(vector2a), length(vector2a));

figure(3)
y = sort(vector2a,'descend');
y0 = sort(vector2b,'descend');
plot(y,x,y0,x);
legend({'Precision','Recall'});
title('Precision and Recall');
xlabel('Intersection Rates');
ylabel('Number of Regions');