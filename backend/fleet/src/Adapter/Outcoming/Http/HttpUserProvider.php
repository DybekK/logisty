<?php

namespace App\Adapter\Outcoming\Http;

use App\Adapter\Outcoming\Http\Dto\UserDto;
use App\Adapter\Outcoming\Http\Dto\UserInvitationDto;
use App\Domain\Model\User;
use App\Domain\Model\UserInvitation;
use App\Domain\Port\UserProvider;
use Core\Domain\Value\User\UserEmail;
use Symfony\Component\Serializer\SerializerInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

readonly class HttpUserProvider implements UserProvider
{
    public function __construct(
        private HttpClientInterface $userClient,
        private SerializerInterface $serializer
    ) {}

    public function getUserByEmail(UserEmail $email): ?User
    {
        $response = $this->userClient->request('GET', '/users',
            ['query' => ['email' => $email->value]]
        );

        if ($response->getStatusCode() === 200)
        {
            return $this->serializer
                ->deserialize($response->getContent(), UserDto::class, 'json')
                ->toModel();
        }

        return null;
    }

    public function getUserInvitationByEmail(UserEmail $email): ?UserInvitation
    {
        $response = $this->userClient->request('GET', '/invitations',
            ['query' => ['email' => $email->value]]
        );

        if ($response->getStatusCode() === 200)
        {
            return $this->serializer
                ->deserialize($response->getContent(), UserInvitationDto::class, 'json')
                ->toModel();
        }

        return null;
    }
}
