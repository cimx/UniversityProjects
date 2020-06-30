function visualizeProperties(BW,stats,num)
    close all
        
    BW2 = bwperim(BW,8);
    
    centers = stats.Centroid;
    areas = stats.Area;
    perimeters = stats.Perimeter;
    
    for c=1: num
        xc = centers(c,1);
        yc = centers(c,2);
        BW = insertText(BW,[xc-30 yc-40],areas(c),'FontSize',50,'TextColor','black','BoxOpacity',0);
    end
    
    figure, imshow(BW);
    
    for c=1: num
        xc = centers(c,1);
        yc = centers(c,2);
        BW = insertText(BW2,[xc-30 yc-40],perimeters(c),'FontSize',50,'TextColor','white','BoxOpacity',0);
    end
    
    figure, imshow(BW);
end