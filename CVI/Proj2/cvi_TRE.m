clear all;

fid = fopen('2015-04-22-18-22-35_tase.gt.txt');
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
%disp(length(data_vector));

%----------------------------GRAPH-TRE-------------------------------------
figure
vector1 = [];
vector2 = [];
vector3 = [];
vector4 = [];
vector5 = [];
vector6 = [];
vector7 = [];
vector8 = [];
for k=1:length(data_vector)
    if k<=105
       vector1 = [vector1 data_vector(k)] 
    end
    if k>105 & k<=210
       vector2 = [vector2 data_vector(k)] 
    end
    
    if k>210 & k<=315
       vector3 = [vector3 data_vector(k)] 
    end
    
    if k>315 & k<=420
       vector4 = [vector4 data_vector(k)] 
    end
    
    if k>420 & k<=525
       vector5 = [vector5 data_vector(k)] 
    end
    
    if k>525 & k<=630
       vector6 = [vector6 data_vector(k)] 
    end
    
    if k>630 & k<=735
       vector7 = [vector7 data_vector(k)] 
    end
    
    if k>735 & k<=840
       vector8 = [vector8 data_vector(k)] 
    end

end


figure(2)
x = linspace(0,length(vector1), length(vector1));
y1 = sort(vector1,'descend');
y2 = sort(vector2,'descend');
y3 = sort(vector3,'descend');
y4 = sort(vector4,'descend');
y5 = sort(vector5,'descend');
y6 = sort(vector6,'descend');
y7 = sort(vector7,'descend');
y8 = sort(vector8,'descend');
plot(y1,x,y2,x,y3,x,y4,x,y5,x,y6,x,y7,x,y8,x);
legend({'group1','group2','group3','group4','group5','group6','group7','group8'})
title('TRE - every 158 frames')
xlabel('Intersection Rates')
ylabel('Number of Regions')