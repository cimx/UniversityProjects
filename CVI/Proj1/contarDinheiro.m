function amount = contarDinheiro(img,num,perimeters)
    close all
    amount = 0;
    
    perimeter_1c = 357;
    perimeter_2c = 420;
    perimeter_5c = 469;
    perimeter_10c = 437;
    perimeter_20c = 493;
    perimeter_50c = 537;
    perimeter_1 = 514;
    perimeter_2 = 569;
    
    for c=1: num
        %disp(perimeters(c));
        if perimeter_1c-5 <= perimeters(c) && perimeters(c) <= perimeter_1c+5
            %disp('1c');
            amount = amount + 0.01;
        elseif perimeter_2c-5 <= perimeters(c) && perimeters(c) <= perimeter_2c+5
            %disp('2c');
            amount = amount + 0.02;
        elseif perimeter_5c-5 <= perimeters(c) && perimeters(c) <= perimeter_5c+5
            %disp('5');
            amount = amount + 0.05;
        elseif perimeter_10c-5 <= perimeters(c) && perimeters(c) <= perimeter_10c+5
            %disp('10');
            amount = amount + 0.1;
        elseif perimeter_20c-5 <= perimeters(c) && perimeters(c) <= perimeter_20c+5
            %disp('20');
            amount = amount + 0.2;
        elseif perimeter_50c-5 <= perimeters(c) && perimeters(c)<= perimeter_50c+5
            %disp('50');
            amount = amount + 0.5;
        elseif perimeter_1-5 <= perimeters(c) && perimeters(c) <= perimeter_1+5
            %disp('1');
            amount = amount + 1;
        elseif perimeter_2-5 <= perimeters(c) && perimeters(c) <= perimeter_2+5
            %disp('2');
            amount = amount + 2;
        end
    end
    
    str = sprintf('%f €',amount);
    img = insertText(img,[5 5],str,'FontSize',50,'TextColor','white', 'BoxColor', 'black','BoxOpacity',0.3);
    figure,imshow(img);
    title('Amount of money in the image');
    
    
    %disp('amount of money: ');
    %disp(amount);
end
    
    
    
    