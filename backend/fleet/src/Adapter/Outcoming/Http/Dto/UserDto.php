<?php

namespace App\Adapter\Outcoming\Http\Dto;

use App\Domain\Model\User;
use Core\Adapter\DtoBehavior;
use Core\Domain\Value\User\UserEmail;
use Core\Domain\Value\User\UserId;

/**
 * @implements DtoBehavior<User>
 */
final readonly class UserDto implements DtoBehavior
{
    public function __construct(
        public string $userId,
        public string $email
    ) {}

    public function toModel(): User
    {
        return new User(
            userId: UserId::of($this->userId),
            email: UserEmail::of($this->email)
        );
    }
}
