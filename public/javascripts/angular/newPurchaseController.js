/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap']);

app.controller('newPurchaseCtrl', function ($scope, $http) {
    $http.get('/getProductsNames').success(function (data) {
        $scope.products = data;
    });
    $scope.bag = [
        {
            name: "",
            id: 0,
            productId: 0,
            packages: 0,
            quantityPerPackage: 0,
            pricePerPackage: 0,
            salePrice: 0
        }
    ];
    $scope.form = {
        purchaseId: 0,
        products: $scope.bag
    }


    $scope.newProduct = function () {
        $scope.bag.push(
            {
                name: "",
                id: 0,
                productId: 0,
                packages: 0,
                quantityPerPackage: 0,
                pricePerPackage: 0,
                salePrice: 0
            }
        )
    }

    $scope.removeProduct = function (index) {
        $scope.bag.splice(index, 1);
    };
    $scope.findProductId = function(n){
        for (var i = 0; i < $scope.products.length; i++) {
            if ($scope.products[i].name == n) return $scope.products[i].id
        }
    }
    $scope.submitForm = function () {
        for (var i = 0; i < $scope.bag.length; i++) {
            $scope.bag[i].id = $scope.findProductId($scope.bag[i].name)
        }
        console.log(JSON.stringify($scope.form));
        $http.post('/compras/crear', JSON.stringify($scope.form)).success(function () {/*success callback*/
        });
    };
});
