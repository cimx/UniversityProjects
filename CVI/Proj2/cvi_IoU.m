clear all;

fid = fopen('2015-04-22-18-22-35_1tase.gt.txt');
nline = fgets(fid);
thr_global = 70;
thr_dif = 15;
min_area = 400;
max_area = 1100;
se = strel('disk',5);

data_vector = [];

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
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector = [data_vector boxes_inter/boxes_junction];
                    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                else
                    boxes_inter=rectint([top_left_labels1(1,1) top_left_labels1(1,2) vessel_area1(1,1) vessel_area1(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area1(1,1) * vessel_area1(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector = [data_vector boxes_inter/boxes_junction];
                    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                end
           elseif (f >= 7650 & f<8000 & lin > 100 & lin < 350 & col > 100 & col < 350)
                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector = [data_vector boxes_inter/boxes_junction];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
            elseif (f > 8000 & lin > 50 & lin < 400 & col > 100 & col < 550)
                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector = [data_vector boxes_inter/boxes_junction];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
            %else
            %    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[1 1 1],'linewidth',2);
            end
        end
    end
   
    nline = fgets(fid);
    drawnow;
end
fclose(fid);

%----------------------------GRAPH-IoU-------------------------------------
vector1 = [];
vector2 = [];
for k=1:length(data_vector)
    if k <= 301
        vector1 = [vector1 data_vector(k)];
    else
        vector2 = [vector2 data_vector(k)];
    end
end

figure(2)
x = sort(vector1,'descend'); 
y = linspace(0,length(vector1), length(vector1));
plot(x,y);
title('IoU');
xlabel('Intersection Rates');
ylabel('Number of Regions');

figure(3)
x = sort(vector2,'descend'); 
y = linspace(0,length(vector2), length(vector2));
plot(x,y);
title('IoU');
xlabel('Intersection Rates');
ylabel('Number of Regions');