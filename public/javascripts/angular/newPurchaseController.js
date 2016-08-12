/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap']);

app.controller('newPurchaseCtrl', function ($scope, $uibModal, $log) {
    $scope.animationsEnabled = true;

    $scope.open = function (size) {

        var modalInstance = $uibModal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'app/views/newPurchase.html',
            controller: 'ModalInstanceCtrl',
            size: size,
            resolve: {
                items: function () {
                    return $scope.items;
                }
            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.toggleAnimation = function () {
        $scope.animationsEnabled = !$scope.animationsEnabled;
    };

});

angular.module('ui.bootstrap.demo').controller('ModalInstanceCtrl', function ($scope, $uibModalInstance) {

    $scope.bag = [
        {
            name: "",
            packages: 0,
            quantityPerPackage: 0,
            purchasePrice: 0,
            salePrice: 0
        },
    ];
    $scope.newProduct = function () {
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

    $scope.ok = function () {
        $uibModalInstance.close($scope.bag);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});
