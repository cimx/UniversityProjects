function distanciaEntreObjetos(img1,num,centers,radii)
    close all

    button = 1;
    figure,imshow(img1);
    title('Press with left button on the coin to get distance information. Right button to exit.');
    while button ~= 3
        [x,y,button] = ginput(1);
        I=img1;
        %Calcular distancia relativa entre objetos
        selected = 1;
        closest_dist =  distanciaPontos(x,y,centers(1,1),centers(1,2));
        for p=2: num
            if  distanciaPontos(x,y,centers(p,1),centers(p,2)) < closest_dist
                selected = p;
                closest_dist = distanciaPontos(x,y,centers(p,1),centers(p,2));
            end
        end

        disp(selected);


        for j=1: num
            xj = centers(j,1);
            yj = centers(j,2);
            if j ~= selected
                dist_centers = distanciaPontos(centers(selected,1),centers(selected,2),xj,yj);
                dist = dist_centers - radii(selected) - radii(j); 
                %line([centers(selected,1) xj], [centers(selected,2) yj],'Color','r');
                I = insertShape(I,'Line',[centers(selected,1), centers(selected,2),xj yj],'LineWidth',3,'Color','red');
                I = insertText(I,[xj-30 yj-30],dist,'FontSize',20,'TextColor','black', 'BoxColor', 'white','BoxOpacity',0.4);
                %disp(selected),disp(j),disp(dist)
            %else
             %   I = insertText(I,[xj-50 yj-60],'X','FontSize',80,'TextColor','red', 'BoxColor', 'white','BoxOpacity',0);
            end 
        end
        close all;
        figure, imshow(I);
        title('Press with  left button on the coin to get distance information. Right button to exit.');
    end
    
    close all;
    figure,imshow(img1);

end


function dist = distanciaPontos(x1,y1,x2,y2)
    dist = sqrt((x1-x2)^2 + (y1-y2)^2);
end