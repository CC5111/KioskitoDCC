/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', []);

app.controller('newPurchaseCtrl', function($scope){
   $scope.numbers = [1,2,3,4,5,6];
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
