function compareAreas(img,num,stats,radii)
    close all
    
    button = 1;
    figure,imshow(img);
    title('Select Object to compare areas');
    [x,y] = ginput(1);
    
    centers = stats.Centroid;
    areas = stats.Area;
    
    selected = 1;
    closest_dist =  distanciaPontos(x,y,centers(1,1),centers(1,2));
    for p=2: num
        if  distanciaPontos(x,y,centers(p,1),centers(p,2)) < closest_dist
            selected = p;
            closest_dist = distanciaPontos(x,y,centers(p,1),centers(p,2));
        end
    end
    
	viscircles([centers(selected,1) centers(selected,2)],radii(selected));
    
   differences = []
   for j=1: num
        xj = centers(j,1);
        yj = centers(j,2);
        if j ~= selected
            area_diff = abs(areas(selected) - areas(j));
            differences.append
            
        end
    end
        

    
    
    
end

function dist = distanciaPontos(x1,y1,x2,y2)
    dist = sqrt((x1-x2)^2 + (y1-y2)^2);
end