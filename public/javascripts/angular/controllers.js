/**
 * Created by ElisaPaz on 11-08-2016.
 */

var app = angular.module('KioskitoCaDCCApp', ['ui.bootstrap', 'nvd3']);

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
    $scope.findProductId = function (n) {
        for (var i = 0; i < $scope.products.length; i++) {
            if ($scope.products[i].name == n) return $scope.products[i].id
        }
    }
    $scope.submitForm = function () {
        for (var i = 0; i < $scope.bag.length; i++) {
            $scope.bag[i].productId = $scope.findProductId($scope.bag[i].name)
        }
        console.log(JSON.stringify($scope.form));
        console.log($scope.form);
        $http.post('/compras/crear', $scope.form).success(function () {/*success callback*/
        });
    };
});

app.controller('newCountCtrl', function ($scope, $http) {
    $scope.expectedEarnings = [];
    $scope.expectedSum = 0;
    $scope.form =
    {
        countId: 0,
        actualEarnings: 0,
        countDetails: []
    };


    $http.get('/get-products-with-stock').success(function (data) {

        for (var i = 0; i < data.length; i++) {
            var product = data[i];
            $scope.expectedEarnings.push(0);
            $scope.form.countDetails.push({
                id: 0,
                productId: product.id,
                product: product.product,
                previousStock: product.stock,
                remainingQuantity: product.stock,
                soldQuantity: 0,
                salePrice: product.salePrice
            });
        };
        $scope.updateExpectedEarnings = function (index) {
            var p = $scope.form.countDetails[index]
            p.soldQuantity = p.previousStock - p.remainingQuantity;
            $scope.expectedEarnings[index] = p.salePrice * p.soldQuantity;
            var aux = 0;
            for (var i = 0; i < $scope.form.countDetails.length; i++) {
                var product = $scope.form.countDetails[i];
                aux += ((product.soldQuantity) * product.salePrice);
            }
            $scope.expectedSum = aux;

        }
    });


    $scope.submitForm = function () {
        console.log(JSON.stringify($scope.form));
        console.log($scope.form);

        $http.post('/conteos/crear', $scope.form).success(function () {/*success callback*/
        });
    };
});

app.controller('expectedVsActualChartCtrl', function($scope){
    $scope.options = {
        chart: {
            type: 'multiBarChart',
            height: 450,
            margin : {
                top: 20,
                right: 20,
                bottom: 45,
                left: 45
            },
            clipEdge: true,
            x: function(d){return d[0];},
            y: function(d){return d[1];},

            showControls: false,
            stacked: false,
            duration: 500,
            xAxis: {
                axisLabel: 'Fecha conteo',
                showMaxMin: false,
                tickFormat: function(d){
                    return d3.time.format('%x')(new Date(d))
                }
            },
            yAxis: {
                axisLabel: '$',
                axisLabelDistance: -20,
                tickFormat: function(d){
                    return d3.format(',.1f')(d);
                }
            }
        }
    };

    $scope.data = [
        {
            key: 'Esperado',
            values:[[1028088000000,1], [1028088085588,1], [1028098085588,6]]
        },
        {
            key: 'Real',
            values: [[1028088000000,3], [1028088085588,5], [1028098085588,5]]
        }
    ];

});

app.controller('lossRatioChartCtrl', function($scope, $http) {
    $scope.options = {
        chart: {
            type: 'lineChart',
            height: 450,
            margin: {
                top: 20,
                right: 20,
                bottom: 50,
                left: 60
            },
            x: function (d) {
                return d[0];
            },
            y: function (d) {
                return d[1];
            },
            useInteractiveGuideline: true,
            showLegend: false,
            xAxis: {
                axisLabel: 'Fecha',
                tickFormat: function (d) {
                    return d3.time.format('%x')(new Date(d))
                },
                showMaxMin: false
            },
            yAxis: {
                axisLabel: 'Calorías',
                tickFormat: function (d) {
                    return d3.format('.02f')(d);
                },
                showMaxMin: false
            }
        },
        title: {
            enable: true,
            text: 'Total de calorías consumidas entre fechas'
        }
    };

    var totalCalories = [];

    $http.get('/calories-per-count').success(function (data) {
        for (var i = 0; i < data.length; i++) {
            totalCalories.push([data[i].date, data[i].totalCalories]);
        }
    });

    $scope.data = [
        {
            "key": "Calorías",
            "values": totalCalories
        }
    ]
})

app.controller('soldProductsChartCtrl', function ($scope) {

    $scope.options = {
        chart: {
            type: 'stackedAreaChart',
            height: 450,
            margin: {
                top: 20,
                right: 20,
                bottom: 50,
                left: 60
            },
            x: function (d) {
                return d[0];
            },
            y: function (d) {
                return d[1];
            },
            useVoronoi: false,
            clipEdge: true,
            duration: 100,
            useInteractiveGuideline: true,
            showControls: false,
            xAxis: {
                axisLabel: 'Fecha',
                showMaxMin: true,
                tickFormat: function (d) {
                    return d3.time.format('%x')(new Date(d))
                }
            },
            yAxis: {
                axisLabel: 'Cantidad',
                showMaxMin: false,
                tickFormat: function (d) {
                    return d3.format(',f')(d);
                }
            },

        },
        title: {
            enable: true,
            text: 'Productos más vendidos'
        }
    };



    $scope.data = [
        {
            "key": "Super 8",
            "values": [[1025409600000, 23.041422681023], [1028088000000, 19.854291255832], [1030766400000, 21.02286281168], [1033358400000, 22.093608385173], [1036040400000, 25.108079299458], [1038632400000, 26.982389242348], [1041310800000, 19.828984957662], [1043989200000, 19.914055036294]]
        },

        {
            "key": "Morocha",
            "values": [[1025409600000, 7.9356392949025], [1028088000000, 7.4514668527298], [1030766400000, 7.9085410566608], [1033358400000, 5.8996782364764], [1036040400000, 6.0591869346923], [1038632400000, 5.9667815800451], [1041310800000, 8.65528925664], [1043989200000, 8.7690763386254]]
        },

        {
            "key": "Golazo",
            "values": [[1025409600000, 7.9149900245423], [1028088000000, 7.0899888751059], [1030766400000, 7.5996132380614], [1033358400000, 8.2741174301034], [1036040400000, 9.3564460833513], [1038632400000, 9.7066786059904], [1041310800000, 10.213363052343], [1043989200000, 10.285809585273]]
        },

        {
            "key": "Jugo Andina",
            "values": [[1025409600000, 13.153938631352], [1028088000000, 12.456410521864], [1030766400000, 12.537048663919], [1033358400000, 13.947386398309], [1036040400000, 14.421680682568], [1038632400000, 14.143238262286], [1041310800000, 12.229635347478], [1043989200000, 12.508479916948]]
        },

        {
            "key": "Lay's",
            "values": [[1025409600000, 9.3433263069351], [1028088000000, 8.4583069475546], [1030766400000, 8.0342398154196], [1033358400000, 8.1538966876572], [1036040400000, 10.743604786849], [1038632400000, 12.349366155851], [1041310800000, 10.742682503899], [1043989200000, 11.360983869935]]
        },

        {
            "key": "NIK",
            "values": [[1025409600000, 5.1162447683392], [1028088000000, 4.2022848306513], [1030766400000, 4.3543715758736], [1033358400000, 5.4641223667245], [1036040400000, 6.0041275884577], [1038632400000, 6.6050520064486], [1041310800000, 5.0154059912793], [1043989200000, 5.1835708554647]]
        },

        {
            "key": "Gansito",
            "values": [[1025409600000, 1.3503144674343], [1028088000000, 1.2232741112434], [1030766400000, 1.3930470790784], [1033358400000, 1.2631275030593], [1036040400000, 1.5842699103708], [1038632400000, 1.9546996043116], [1041310800000, 0.8504048300986], [1043989200000, 0.85340686311353]]
        }

    ]

});

app.controller('totalConsCaloriesChartCtrl', function($scope, $http){
    $scope.options = {
        chart: {
            type: 'lineChart',
            height: 450,
            margin: {
                top: 20,
                right: 50,
                bottom: 50,
                left: 80
            },
            x: function (d) {
                return d[0];
            },
            y: function (d) {
                return d[1];
            },
            useInteractiveGuideline: true,
            showLegend: false,

            xAxis: {
                axisLabel: 'Fecha',
                axisLabelDistance: 10,
                tickFormat: function (d) {
                    return d3.time.format('%x')(new Date(d))
                },
                showMaxMin: true,
                ticks:10,
                tickPadding: 10

            },

            forceY: 0,
            yAxis: {
                axisLabel: 'Calorías',
                axisLabelDistance: 5,

                tickFormat: function (d) {
                    return d3.format('.02f')(d);
                },
                showMaxMin: true,
                ticks: 10
            }
        },
        title: {
            enable: true,
            text: 'Total de calorías consumidas entre fechas'
        }
    };

    var totalCalories = [];

    $http.get('/calories-per-count').success(function (data) {
        for (var i = 0; i < data.length; i++) {
            totalCalories.push([data[i].date, data[i].totalCalories]);
        }
    });

    $scope.data = [
        {
            "key": "Calorías",
            "values": totalCalories
        }
    ]
});

app.controller('detailedConsCaloriesChartCtrl', function ($scope) {


})


