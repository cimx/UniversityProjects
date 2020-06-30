
function [L,num,stats,morph] = tratamentoImagem(img1)

    imgg1 = rgb2gray(img1);
    figure,imshow(imgg1)
    
    level1 = graythresh(imgg1);
    imgbw1 = im2bw(imgg1, level1);
    figure,imshow(imgbw1)
    se = strel('disk',20);
    IM2 = imclose(imgbw1,se);
    morph = bwmorph(IM2,'clean');
    figure,imshow(morph);
    

    [L,num] = bwlabel (morph);
    disp('Number of objects: '), disp(num);    
    
    stats = regionprops('table',morph,'Centroid','MajorAxisLength','MinorAxisLength', 'area', 'perimeter');
    
    diameters = mean([stats.MajorAxisLength stats.MinorAxisLength],2);
    radii = diameters/2;
    centers = stats.Centroid;
    centroids = cat(1, stats.Centroid);
    
    text = sprintf('%d objects',num);
    img = insertText(img1,[5 5],text,'FontSize',50,'TextColor','white', 'BoxColor', 'black','BoxOpacity',0.3);
    figure,imshow(img);
    
    hold on
    viscircles(centers,radii);
    plot(imgca,centroids(:,1), centroids(:,2), 'r.');
    hold off
    
    %[centers1, radii1] = imfindcircles(morph,[50 400],'ObjectPolarity','bright');
    %viscircles(centers1, radii1,'Color','c'); 
        
end