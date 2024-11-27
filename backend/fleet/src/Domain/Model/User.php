<?php

namespace App\Domain\Model;

use Core\Domain\Value\User\UserEmail;
use Core\Domain\Value\User\UserId;

final readonly class User
{
    public function __construct(
        public UserId    $userId,
        public UserEmail $email
    ) {}
}
