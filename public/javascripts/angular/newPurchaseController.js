/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap']);

app.controller('newPurchaseCtrl', function($scope, $http){
    $http.get('/getProductsNames').success(function(data){
        $scope.names = data;
    });

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
