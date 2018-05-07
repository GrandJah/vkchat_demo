(function () {
    "use strict";

    angular.module('app', [
        'ng',
        'ngResource'
    ])
        .run(function ($rootScope, $compile) {

            $rootScope.appData = appData || {};

        })
        .directive('chatMessage', function () {
            return {
                templateUrl: '/chat-message.html',
                scope: {
                    message: '='
                }
            }
        })
        .controller('AppController', function ($rootScope, $scope, $http) {

            $scope.master = {
                message: '',
                interlocutorId: ''
            };

            $scope.messagesBuffer = [];

            $scope.isPolling = false;

            $scope.lasdMessageId = 0;

            var isMessageBuffered = function (msgId) {
                return !!$scope.messagesBuffer.filter(function (msg) {
                    return msg.id === msgId;
                }).length;
            };

            var pollNext = function () {
                $http.get('/api/chat/' + $scope.master.interlocutorId + '/poll?lastId=' + $scope.lasdMessageId)
                    .then(function (r) {
                        return r.data
                    })
                    .then(function (r) {
                        (r.messages || []).filter(function (msg) {
                            return !isMessageBuffered(msg.id);
                        }).forEach(function (msg) {
                            $scope.pushMessage(msg);
                        });
                        pollNext();
                    })
                    .catch(function (e) {
                        if (e.status !== 404) {
                            pollNext();
                        }
                    });
            };

            $scope.startPolling = function () {
                if (!$scope.isPolling) {
                    $scope.isPolling = true;
                    pollNext();
                }
            };

            $scope.sendMessage = function () {
                if (!$scope.master.message) {
                    return;
                }
                $http.post('/api/chat/' + $scope.master.interlocutorId + '/send', $scope.master.message)
                    .then(function (r) {
                        if (r.data.message) {
                            $scope.startPolling();
                            $scope.master.message = "";
                        }
                    });
            };

            $scope.pushMessage = function (message) {
                $scope.lasdMessageId = $scope.lasdMessageId > message.id ? $scope.lasdMessageId : message.id;
                message.sent = $rootScope.appData.selfId === message.sender.id;
                $scope.messagesBuffer.push(message);
            };

            $scope.$watch(function () {
                    return document.body.scrollHeight;
                },
                function (newValue) {
                    document.body.scroll(0, newValue)
                });


        });
})();