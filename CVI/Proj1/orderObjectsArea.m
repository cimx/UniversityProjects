function ordered_stats = orderObjectsArea(img,num,stats)

    ordered_stats = sortrows(stats,'Area');
    disp(ordered_stats);
    centers = ordered_stats.Centroid;
    
    for c=1: num
        xc = centers(c,1);
        yc = centers(c,2);
        img = insertText(img,[xc-30 yc-40],c,'FontSize',50,'TextColor','red','BoxOpacity',0);
    end

    figure, imshow(img);
    title('Coins ordered by area (from lowest to highest)');
    
end