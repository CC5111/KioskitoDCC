/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap']);

app.controller('newPurchaseCtrl', function($scope){
   $scope.names = ['Super 8', 'Chocman', 'Morochas', 'Papas Lays', 'Ramitas', 'Golazo', 'Sopa Maruchan'];
   $scope.bag = [
     {
        name: "",
        packages: 0,
        quantityPerPackage: 0,
        purchasePrice: 0,
        salePrice: 0
     },
   ];


   $scope.newProduct = function(){
        $scope.bag.push(
             {
                name: "",
                packages: 0,
                quantityPerPackage: 0,
                purchasePrice: 0,
                salePrice: 0
             }
        )
   }

   $scope.removeProduct = function (index) {
       $scope.bag.splice(index, 1);
   };
});
