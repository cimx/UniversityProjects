clear all;

fid = fopen('2015-04-22-18-22-35_tase.gt.txt');
nline = fgets(fid);
thr_global = 70;
thr_dif = 15;
min_area = 400;
max_area = 1100;
se = strel('disk',5);

data_vector = [];    %normal
data_vector0 = [];   %+10 vertical
data_vector1 = [];   %+10 horizontal
data_vector2 = [];   %+10 cima-direita
data_vector3 = [];   %+10 cima-esquerda
data_vector4 = [];   %-10 vertical
data_vector5 = [];   %-10 horizontal
data_vector6 = [];   %-10 cima-direita
data_vector7 = [];   %-10 cima-esquerda
data_vector8 = [];   %scale 0.8
data_vector9 = [];   %scale 0.9
data_vector10 = [];  %scale 1.1
data_vector11 = [];  %scale 1.2


while ischar(nline)
     hi = strsplit(nline);
     frame_id = hi(1,1);
     top_left_labels = [str2double(hi(1,2)),str2double(hi(1,3))];
     vessel_area = [str2double(hi(1,4)),str2double(hi(1,5))];
     f = str2double(frame_id);
     frame = imread(sprintf('frames/frame-%.d.tif',f)); 
     imshow(frame);
     rectangle('Position',[top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], 'EdgeColor',[1 0 0],'linewidth', 2 );
     
     nline1 = fgets(fid);
     if f < 7550
        hi1 = strsplit(nline1);
        top_left_labels1 = [str2double(hi1(1,2)),str2double(hi1(1,3))];
        vessel_area1 = [str2double(hi1(1,4)),str2double(hi1(1,5))];
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
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector0 = [data_vector0 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector1 = [data_vector1 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector2 = [data_vector2 boxes_inter/boxes_junction];  
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector3 = [data_vector3 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector4 = [data_vector4 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector5 = [data_vector5 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector6 = [data_vector6 boxes_inter/boxes_junction]; 
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector7 = [data_vector7 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.8 vessel_area(1,2)*0.8], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*0.8 * vessel_area(1,2)*0.8) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector8 = [data_vector8 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.9 vessel_area(1,2)*0.9], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*0.9 * vessel_area(1,2)*0.9) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector9 = [data_vector9 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.1 vessel_area(1,2)*1.1], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*1.1 * vessel_area(1,2)*1.1) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector10 = [data_vector10 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.2 vessel_area(1,2)*1.2], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*1.2 * vessel_area(1,2)*1.2) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector11 = [data_vector11 boxes_inter/boxes_junction];
                    
                else
                    boxes_inter=rectint([top_left_labels1(1,1) top_left_labels1(1,2) vessel_area1(1,1) vessel_area1(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area1(1,1) * vessel_area1(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector = [data_vector boxes_inter/boxes_junction];
                    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector0 = [data_vector0 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector1 = [data_vector1 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector2 = [data_vector2 boxes_inter/boxes_junction];  
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector3 = [data_vector3 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector4 = [data_vector4 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector5 = [data_vector5 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector6 = [data_vector6 boxes_inter/boxes_junction]; 
                    
                    boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector7 = [data_vector7 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.8 vessel_area(1,2)*0.8], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*0.8 * vessel_area(1,2)*0.8) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector8 = [data_vector8 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.9 vessel_area(1,2)*0.9], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*0.9 * vessel_area(1,2)*0.9) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector9 = [data_vector9 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.1 vessel_area(1,2)*1.1], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*1.1 * vessel_area(1,2)*1.1) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector10 = [data_vector10 boxes_inter/boxes_junction];
                    
                    boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.2 vessel_area(1,2)*1.2], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                    boxes_junction = (vessel_area(1,1)*1.2 * vessel_area(1,2)*1.2) + (dWindow(2)* dWindow(1)) - boxes_inter;
                    data_vector11 = [data_vector11 boxes_inter/boxes_junction];
                    
                end
           elseif (f >= 7650 & f<8000 & lin > 100 & lin < 350 & col > 100 & col < 350)
                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector = [data_vector boxes_inter/boxes_junction];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                
                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector0 = [data_vector0 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector1 = [data_vector1 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector2 = [data_vector2 boxes_inter/boxes_junction];  

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector3 = [data_vector3 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector4 = [data_vector4 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector5 = [data_vector5 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector6 = [data_vector6 boxes_inter/boxes_junction]; 

                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector7 = [data_vector7 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.8 vessel_area(1,2)*0.8], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*0.8 * vessel_area(1,2)*0.8) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector8 = [data_vector8 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.9 vessel_area(1,2)*0.9], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*0.9 * vessel_area(1,2)*0.9) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector9 = [data_vector9 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.1 vessel_area(1,2)*1.1], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*1.1 * vessel_area(1,2)*1.1) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector10 = [data_vector10 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.2 vessel_area(1,2)*1.2], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*1.2 * vessel_area(1,2)*1.2) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector11 = [data_vector11 boxes_inter/boxes_junction];

            elseif (f > 8000 & lin > 50 & lin < 400 & col > 100 & col < 550)
                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector = [data_vector boxes_inter/boxes_junction];
                rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[0 1 0],'linewidth',2);
                
                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector0 = [data_vector0 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector1 = [data_vector1 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector2 = [data_vector2 boxes_inter/boxes_junction];  

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)+top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector3 = [data_vector3 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2) vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector4 = [data_vector4 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector5 = [data_vector5 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1)-top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector6 = [data_vector6 boxes_inter/boxes_junction]; 

                boxes_inter=rectint([top_left_labels(1,1)+top_left_labels(1,1)*0.1 top_left_labels(1,2)-top_left_labels(1,2)*0.1 vessel_area(1,1) vessel_area(1,2)], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1) * vessel_area(1,2)) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector7 = [data_vector7 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.8 vessel_area(1,2)*0.8], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*0.8 * vessel_area(1,2)*0.8) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector8 = [data_vector8 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*0.9 vessel_area(1,2)*0.9], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*0.9 * vessel_area(1,2)*0.9) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector9 = [data_vector9 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.1 vessel_area(1,2)*1.1], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*1.1 * vessel_area(1,2)*1.1) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector10 = [data_vector10 boxes_inter/boxes_junction];

                boxes_inter=rectint([top_left_labels(1,1) top_left_labels(1,2) vessel_area(1,1)*1.2 vessel_area(1,2)*1.2], [upLPoint(2) upLPoint(1) dWindow(2) dWindow(1)]);
                boxes_junction = (vessel_area(1,1)*1.2 * vessel_area(1,2)*1.2) + (dWindow(2)* dWindow(1)) - boxes_inter;
                data_vector11 = [data_vector11 boxes_inter/boxes_junction];
                
                
            %else
            %    rectangle('Position',[fliplr(upLPoint) fliplr(dWindow)],'EdgeColor',[1 1 1],'linewidth',2);
            end
        end
    end
   
    nline = fgets(fid);
    drawnow;
end
fclose(fid);

%----------------------------GRAPH-SRE-------------------------------------
vectora = [];
vectorb = [];
vector0a = [];
vector0b = [];
vector1a = [];
vector1b = [];
vector2a = [];
vector2b = [];
vector3a = [];
vector3b = [];
vector4a = [];
vector4b = [];
vector5a = [];
vector5b = [];
vector6a = [];
vector6b = [];
vector7a = [];
vector7b = [];
vector8a = [];
vector8b = [];
vector9a = [];
vector9b = [];
vector10a = [];
vector10b = [];
vector11a = [];
vector11b = [];
for k=1:length(data_vector)
    if k <= 301
        vectora = [vectora data_vector(k)] 
    else
        vectorb = [vectorb data_vector(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector0a = [vector0a data_vector0(k)] 
    else
        vector0b = [vector0b data_vector0(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector1a = [vector1a data_vector1(k)] 
    else
        vector1b = [vector1b data_vector1(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector2a = [vector2a data_vector2(k)] 
    else
        vector2b = [vector2b data_vector2(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector3a = [vector3a data_vector3(k)] 
    else
        vector3b = [vector3b data_vector3(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector4a = [vector4a data_vector4(k)] 
    else
        vector4b = [vector4b data_vector4(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector5a = [vector5a data_vector5(k)] 
    else
        vector5b = [vector5b data_vector5(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector6a = [vector6a data_vector6(k)] 
    else
        vector6b = [vector6b data_vector6(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector7a = [vector7a data_vector7(k)] 
    else
        vector7b = [vector7b data_vector7(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector8a = [vector8a data_vector8(k)] 
    else
        vector8b = [vector8b data_vector8(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector9a = [vector9a data_vector9(k)] 
    else
        vector9b = [vector9b data_vector9(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector10a = [vector10a data_vector10(k)] 
    else
        vector10b = [vector10b data_vector10(k)] 
    end
end
for k=1:length(data_vector)
    if k <= 301
        vector11a = [vector11a data_vector11(k)] 
    else
        vector11b = [vector11b data_vector11(k)] 
    end
end

%Frames más
x = linspace(0,length(vectora), length(vectora));
y = sort(vectora,'descend');
y0 = sort(vector0a,'descend');
y1 = sort(vector1a,'descend');
y2 = sort(vector2a,'descend');
y3 = sort(vector3a,'descend');
y4 = sort(vector4a,'descend');
y5 = sort(vector5a,'descend');
y6 = sort(vector6a,'descend');
y7 = sort(vector7a,'descend');
y8 = sort(vector8a,'descend');
y9 = sort(vector9a,'descend');
y10 = sort(vector10a,'descend');
y11 = sort(vector11a,'descend');
figure(2)
plot(y,x,y0,x,y1,x,y2,x,y3,x,y4,x,y5,x,y6,x,y7,x,y8,x,y9,x,y10,x,y11,x);
legend({'normal','+10%h','+10%v','+10%cd','+10%ce','-10%h','-10%v','-10%cd','-10%ce','scale0.8','scale0.9','scale1.1','scale1.2'})
title('SRE')
xlabel('Intersection Rates')
ylabel('Number of Regions')

%Frames boas
x = linspace(0,length(vectorb), length(vectorb));
y = sort(vectorb,'descend');
y0 = sort(vector0b,'descend');
y1 = sort(vector1b,'descend');
y2 = sort(vector2b,'descend');
y3 = sort(vector3b,'descend');
y4 = sort(vector4b,'descend');
y5 = sort(vector5b,'descend');
y6 = sort(vector6b,'descend');
y7 = sort(vector7b,'descend');
y8 = sort(vector8b,'descend');
y9 = sort(vector9b,'descend');
y10 = sort(vector10b,'descend');
y11 = sort(vector11b,'descend');

figure(3)
plot(y,x,y0,x,y1,x,y2,x,y3,x,y4,x,y5,x,y6,x,y7,x,y8,x,y9,x,y10,x,y11,x);
legend({'normal','+10%h','+10%v','+10%cd','+10%ce','-10%h','-10%v','-10%cd','-10%ce','scale0.8','scale0.9','scale1.1','scale1.2'})
title('SRE')
xlabel('Intersection Rates')
ylabel('Number of Regions')