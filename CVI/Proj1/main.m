clear all, close all

img1 = imread('Moedas1.jpg');
%img1 = imread('Moedas2.jpg');
%img1 = imread('Moedas3.jpg');
%img1 = imread('Moedas4.jpg');

[L,num,stats,BW] = tratamentoImagem(img1);

ordered_stats = orderObjectsArea(img1,num,stats);

diameters = mean([ordered_stats.MajorAxisLength ordered_stats.MinorAxisLength],2);
radii = diameters/2;

%distanciaEntreObjetos(img1,num,stats.Centroid,radii);

%amount = contarDinheiro(img1,num,stats.Perimeter);

%drawHistogram(img1);

%derivadaDasFronteiras(img1);

%visualizeProperties(BW,stats,num)

compareAreas(img1,num,ordered_stats,radii);